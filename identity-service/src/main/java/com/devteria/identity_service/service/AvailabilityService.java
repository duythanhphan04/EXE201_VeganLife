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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final UserService userService;
    @Autowired
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
                            .status(AppointmentStatus.AVAILABLE)
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

    public List<Availability> getCoachBookedSlotsByStatus(
            String userID, AppointmentStatus status) {
        return availabilityRepository.getAvailabilitiesByCoach_UserIDAndStatus(userID, status);
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

    public List<Availability> getCoachAvailableSlots(String userID) {
        return availabilityRepository.findByCoach_UserID(userID);
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
            if (status.equals(AppointmentStatus.AVAILABLE)) {
                availability.setStatus(AppointmentStatus.AVAILABLE);
                String reason = request.getReason();
                availability.setReason(reason);
                availabilityRepository.save(availability);
                Appointment appointment =
                        appointmentRepository.findByCoachUsernameAndAppointmentDateTime(loginCoach, slotCancel);
                if (appointment != null) {
                    appointment.setStatus(AppointmentStatus.AVAILABLE);
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

    public Availability deleteAvailability(String coachID, String availabilityID) {
        Availability availability = availabilityRepository.findById(availabilityID).orElse(null);
        if (availability == null || !availability.getCoach().getUserID().equals(coachID)) {
            throw new IllegalArgumentException("Availability not found or coach mismatch");
        }
        availabilityRepository.delete(availability);
        return availability;
    }
}
