package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.CreateAvailabilityRequest;
import com.devteria.identity_service.dto.UpdateAvailabilityRequest;
import com.devteria.identity_service.entity.Appointment;
import com.devteria.identity_service.entity.Availability;
import com.devteria.identity_service.entity.MailBody;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.AppointmentStatus;
import com.devteria.identity_service.mapper.AvailabilityMapper;
import com.devteria.identity_service.repository.AppointmentRepository;
import com.devteria.identity_service.repository.AvailabilityRepository;
import com.devteria.identity_service.response.AvailabilityResponse;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailabilityService {
  private final AvailabilityRepository availabilityRepository;
  private final UserService userService;
  private final AvailabilityMapper availabilityMapper;
  private final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
  private final EmailSenderService emailSenderService;
  private final AppointmentRepository appointmentRepository;

  @Transactional
  public List<AvailabilityResponse> createCoachAvailabilities(CreateAvailabilityRequest request) {
    String loginCoach = userService.getLoggedInUsername();
    User coach = userService.getUserEntity(loginCoach);
    List<String> requestedDateTimesString = request.getAvailabilityDatetime();
    List<Instant> requestedAvailabilityTimes =
        requestedDateTimesString.stream().map(Instant::parse).toList();
    Set<Instant> existingTimesForCoach =
        coach.getAvailabilities().stream()
            .map(Availability::getAvailabilityDatetime)
            .collect(Collectors.toSet());
    List<Availability> newAvailabilitiesToPersist = new ArrayList<>();
    for (Instant time : requestedAvailabilityTimes) {
      if (existingTimesForCoach.contains(time)) {
        System.out.println(
            "Availability at " + time + " already exists for coach " + coach.getUsername());
        continue;
      }
      Availability availability =
          Availability.builder()
              .coach(coach)
              .availabilityDatetime(time)
              .status(AppointmentStatus.SCHEDULED)
              .build();
      newAvailabilitiesToPersist.add(availability);
    }
    List<Availability> savedAvailabilities =
        availabilityRepository.saveAll(newAvailabilitiesToPersist);
    coach.getAvailabilities().addAll(newAvailabilitiesToPersist);
    return savedAvailabilities.stream()
        .map(availabilityMapper::toAvailabilityResponse)
        .collect(Collectors.toList());
  }

  public List<LocalDateTime> getCoachBookedSlotsByStatus(
      String username, String from, String to, AppointmentStatus status) {
    LocalDate fromDate = LocalDate.parse(from);
    LocalDate toDate = LocalDate.parse(to);
    Instant fromInstant = fromDate.atStartOfDay(VIETNAM_ZONE).toInstant();
    Instant toInstant = toDate.atTime(LocalTime.MAX).atZone(VIETNAM_ZONE).toInstant();
    List<Availability> scheduledSlots =
        getByCoachUsernameAndAvailabilityDateTimeBetween(username, fromInstant, toInstant);
    return scheduledSlots.stream()
        .filter(slot -> slot.getStatus().equals(status))
        .map(dateTime -> LocalDateTime.ofInstant(dateTime.getAvailabilityDatetime(), VIETNAM_ZONE))
        .toList();
  }

  public List<Availability> getByCoachUsernameAndAvailabilityDateTimeBetween(
      String username, Instant from, Instant to) {
    return availabilityRepository.findByCoach_UsernameAndAvailabilityDatetimeBetween(
        username, from, to);
  }

  public Availability getCoachAvailabilityEntityByAvailabilityDateTime(
      String username, Instant availabilityDateTime) {
    return availabilityRepository.findByCoach_UsernameAndAvailabilityDatetime(
        username, availabilityDateTime);
  }

  public List<LocalDateTime> getCoachAvailableSlots(String username, String from, String to) {
    LocalDate fromLocalDate = LocalDate.parse(from);
    LocalDate toLocalDate = LocalDate.parse(to);
    Instant fromInstant = fromLocalDate.atStartOfDay(VIETNAM_ZONE).toInstant();
    Instant toInstant = toLocalDate.atTime(LocalTime.MAX).atZone(VIETNAM_ZONE).toInstant();
    List<Availability> unavailableSlots =
        getByCoachUsernameAndAvailabilityDateTimeBetween(username, fromInstant, toInstant);
    Set<Instant> unavailableInstantTimes =
        unavailableSlots.stream()
            .map(Availability::getAvailabilityDatetime)
            .collect(Collectors.toSet());
    List<LocalDateTime> availableSlotsLocal = new ArrayList<>();
    for (LocalDate date = fromLocalDate; !date.isAfter(toLocalDate); date = date.plusDays(1)) {
      for (int hour = 8; hour < 17; hour++) {
        if (hour == 12) {
          continue;
        }
        LocalDateTime slotLocal = LocalDateTime.of(date, LocalTime.of(hour, 0));
        Instant potentialSlotInstant = slotLocal.atZone(VIETNAM_ZONE).toInstant();
        if (!unavailableInstantTimes.contains(potentialSlotInstant)) {
          if (potentialSlotInstant.isAfter(Instant.now())) {
            availableSlotsLocal.add(slotLocal);
          }
        }
      }
    }
    return availableSlotsLocal;
  }

  public void confirmConsultantScheduledSlot(
      String coachUsername, Instant appointmentDateTime, AppointmentStatus appointmentStatus) {
    Availability availability =
        getCoachAvailabilityEntityByAvailabilityDateTime(coachUsername, appointmentDateTime);
    availability.setStatus(appointmentStatus);
    availabilityRepository.save(availability);
  }

  @Transactional
  public AvailabilityResponse cancelCoachScheduledSlot(
      AppointmentStatus status, UpdateAvailabilityRequest request) throws MessagingException {
    String loginCoach = userService.getLoggedInUsername();
    Instant slotCancel = Instant.parse(request.getAvailabilityDateTime());
    Availability availability =
        getCoachAvailabilityEntityByAvailabilityDateTime(loginCoach, slotCancel);
    if (availability != null) {
      if (status.equals(AppointmentStatus.CANCELLED)) {
        availability.setStatus(AppointmentStatus.CANCELLED);
        String reason = request.getReason();
        availability.setReason(reason);
        availabilityRepository.save(availability);
        Appointment appointment =
            appointmentRepository.findByCoachUsernameAndAppointmentDateTime(loginCoach, slotCancel);
        if (appointment != null) {
          appointment.setStatus(AppointmentStatus.CANCELLED);
          appointmentRepository.save(appointment);
          User user = appointment.getUser();
          String userEmail = user.getEmail();
          User coach = appointment.getCoach();
          String coachEmail = coach.getEmail();
          String[] recipients = {userEmail, coachEmail};
          MailBody mailBody =
              MailBody.builder()
                  .subject("Thông báo hủy lịch hẹn")
                  .to(recipients)
                  .body(
                      "Lịch hẹn vào lúc "
                          + slotCancel.toString()
                          + " đã bị huỷ. Lý do: "
                          + request.getReason())
                  .build();
          emailSenderService.sendEmail(mailBody);
        }
        return availabilityMapper.toAvailabilityResponse(availability);
      } else {
        throw new IllegalArgumentException("Invalid status for cancellation");
      }
    } else {
      throw new IllegalArgumentException("No availability found for the given date and time");
    }
  }

  public AvailabilityResponse cancelCoachScheduledSlots(
      AppointmentStatus status,
      com.devteria.identity_service.controller.@Valid UpdateAvailabilityRequest request)
      throws MessagingException {
    String loginCoach = userService.getLoggedInUsername();
    Instant slotCancel = Instant.parse(request.getAvailabilityDateTime());
    Availability availability =
        getCoachAvailabilityEntityByAvailabilityDateTime(loginCoach, slotCancel);
    if (availability != null) {
      if (status.equals(AppointmentStatus.CANCELLED)) {
        availability.setStatus(AppointmentStatus.CANCELLED);
        String reason = request.getReason();
        availability.setReason(reason);
        availabilityRepository.save(availability);
        Appointment appointment =
            appointmentRepository.findByCoachUsernameAndAppointmentDateTime(loginCoach, slotCancel);
        if (appointment != null) {
          appointment.setStatus(AppointmentStatus.CANCELLED);
          appointmentRepository.save(appointment);
          // Email sending logic can be added here if needed
          User user = appointment.getUser();
          String userEmail = user.getEmail();
          User coach = appointment.getCoach();
          String coachEmail = coach.getEmail();
          String[] recipients = {userEmail, coachEmail};
          MailBody mailBody =
              MailBody.builder()
                  .subject("Thông báo hủy lịch hẹn")
                  .to(recipients)
                  .body(
                      "Lịch hẹn vào lúc "
                          + slotCancel.toString()
                          + " đã bị huỷ. Lý do: "
                          + request.getReason())
                  .build();
          emailSenderService.sendEmail(mailBody);
        }
        return availabilityMapper.toAvailabilityResponse(availability);
      } else {
        throw new IllegalArgumentException("Invalid status for cancellation");
      }
    } else {
      throw new IllegalArgumentException("No availability found for the given date and time");
    }
  }
}
