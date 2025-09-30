package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.CreateAppointmentRequest;
import com.devteria.identity_service.dto.UpdateAppointmentRequest;
import com.devteria.identity_service.entity.Appointment;
import com.devteria.identity_service.entity.Availability;
import com.devteria.identity_service.entity.MailBody;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.AppointmentStatus;
import com.devteria.identity_service.mapper.AppointmentMapper;
import com.devteria.identity_service.repository.AppointmentRepository;
import com.devteria.identity_service.repository.AvailabilityRepository;
import com.devteria.identity_service.response.AppointmentResponse;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentService {
  private final AppointmentRepository appointmentRepository;
  @Autowired private final AppointmentMapper appointmentMapper;
  private final UserService userService;
  private final AvailabilityService availabilityService;
  private final AvailabilityRepository availabilityRepository;
  private final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
  private final EmailSenderService emailSenderService;
  private final GoogleCalendarService googleCalendarService;

  public AppointmentResponse createAppointment(CreateAppointmentRequest request)
      throws IOException {
    // Lấy user đang login trong hệ thống
    String loginUsername = userService.getLoggedInUsername();
    User user = userService.getUserEntity(loginUsername);
    // Tạo lịch hẹn trên Google Calendar và lấy link Google Meet
    String link =
        googleCalendarService.createGGMeetAppointment(
            request,
            user.getEmail(),
            request.getGoogleAccessToken() // cần có field googleAccessToken trong request
            );

    // Tạo entity Appointment
    Appointment appointment = appointmentMapper.toEntity(request);
    appointment.setLink(link);

    Instant appointmentDateTime = Instant.parse(request.getAppointmentDateTime());
    appointment.setAppointmentDateTime(appointmentDateTime);
    appointment.setStatus(AppointmentStatus.SCHEDULED);
    appointment.setUser(user);

    // Set coach
    User coach = userService.getUserEntityByID(request.getCoachID());
    String coachUsername = coach.getUsername();
    appointment.setCoach(coach);

    // Lưu DB
    appointmentRepository.save(appointment);

    // Xác nhận slot của coach đã được book
    availabilityService.confirmConsultantScheduledSlot(
        coachUsername, appointmentDateTime, AppointmentStatus.CONFIRMED);

    return appointmentMapper.toResponse(appointment);
  }

  public List<AppointmentResponse> getAllAppointments() {
    List<Appointment> appointments = appointmentRepository.findAll();
    return appointments.stream().map(appointmentMapper::toResponse).toList();
  }

  public List<AppointmentResponse> getUserAppointments(String username) {
    List<Appointment> appointments =
        appointmentRepository.findByUser_UsernameOrderByAppointmentDateTimeAsc(username);
    return appointments.stream().map(appointmentMapper::toResponse).toList();
  }

  public List<AppointmentResponse> getCoachAppointments(String coachUsername) {
    List<Appointment> appointments =
        appointmentRepository.findByCoach_UsernameOrderByAppointmentDateTimeAsc(coachUsername);
    return appointments.stream().map(appointmentMapper::toResponse).toList();
  }

  public Appointment getAppointmentById(String appointmentId) {
    return appointmentRepository
        .findById(appointmentId)
        .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
  }

  public AppointmentResponse getAppointmentResponseById(String appointmentId) {
    Appointment appointment = getAppointmentById(appointmentId);
    return appointmentMapper.toResponse(appointment);
  }

  public AppointmentResponse updateAppointment(
      String appointmentId, UpdateAppointmentRequest request) throws IOException {
    Appointment appointment = getAppointmentById(appointmentId);
    appointment.setAppointmentDateTime(Instant.parse(request.getAppointmentDateTime()));
    appointment.setNotes(request.getNotes());
    appointment.setStatus(request.getStatus());
    appointmentRepository.save(appointment);
    return appointmentMapper.toResponse(appointment);
  }

  public List<AppointmentResponse> getUserTodayAppointments(String username) {
    ZoneId zone = ZoneId.systemDefault();
    Instant startOfDay = LocalDate.now().atStartOfDay(zone).toInstant();
    Instant endOfDay = LocalDate.now().plusDays(1).atStartOfDay(zone).toInstant();
    List<Appointment> appointments =
        appointmentRepository
            .findByUser_UsernameAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeDesc(
                username, startOfDay, endOfDay);
    return appointments.stream().map(appointmentMapper::toResponse).toList();
  }

  public List<AppointmentResponse> getCoachTodayAppointments(String coachUsername) {
    ZoneId zone = ZoneId.systemDefault();
    Instant startOfDay = LocalDate.now().atStartOfDay(zone).toInstant();
    Instant endOfDay = LocalDate.now().plusDays(1).atStartOfDay(zone).toInstant();
    List<Appointment> appointments =
        appointmentRepository
            .findByCoach_UsernameAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeDesc(
                coachUsername, startOfDay, endOfDay);
    return appointments.stream().map(appointmentMapper::toResponse).toList();
  }

  public List<AppointmentResponse> getAllAppointmentsByDateDuration(Instant start, Instant end) {
    List<Appointment> appointments = appointmentRepository.findByCreatedAtBetween(start, end);
    return appointments.stream().map(appointmentMapper::toResponse).toList();
  }

  public long countCoachAppointments(String coachUsername) {
    return appointmentRepository.countByCoach_Username(coachUsername);
  }

  private List<Appointment> getByUser_UsernameAndAppointmentDateTimeBetween(
      String username, Instant startOfDay, Instant endOfDay) {
    return appointmentRepository
        .findByUser_UsernameAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeDesc(
            username, startOfDay, endOfDay);
  }

  public List<LocalDateTime> getUserBookedAppointmentByStatus(
      String username, AppointmentStatus status, String from, String to) {
    LocalDate startDate = LocalDate.parse(from);
    LocalDate endDate = LocalDate.parse(to);
    Instant startOfDay = startDate.atStartOfDay(VIETNAM_ZONE).toInstant();
    Instant endOfDay = endDate.atTime(LocalTime.MAX).atZone(VIETNAM_ZONE).toInstant();
    List<Appointment> appointments =
        getByUser_UsernameAndAppointmentDateTimeBetween(username, startOfDay, endOfDay);
    return appointments.stream()
        .filter(appointment -> appointment.getStatus() == status)
        .map(
            appointment ->
                LocalDateTime.ofInstant(appointment.getAppointmentDateTime(), VIETNAM_ZONE))
        .toList();
  }

  @Transactional
  public AppointmentResponse cancelUserScheduledAppointment(
      AppointmentStatus status, UpdateAppointmentRequest request)
      throws IOException, MessagingException {
    String loginUsername = userService.getLoggedInUsername();
    Instant appointmentDateTime = Instant.parse(request.getAppointmentDateTime());
    Appointment appointment =
        getByUser_UsernameAndAppointmentDateTime(loginUsername, appointmentDateTime);
    if (appointment != null) {
      if (status.equals(AppointmentStatus.CANCELLED)) {
        appointment.setStatus(AppointmentStatus.CANCELLED);
        String reason = request.getNotes();
        appointment.setNotes(reason);
        appointmentRepository.save(appointment);
        Availability availability =
            availabilityService.getCoachAvailabilityEntityByAvailabilityDateTime(
                appointment.getCoach().getUsername(), appointmentDateTime);
        if (availability != null) {
          availability.setStatus(AppointmentStatus.AVAILABLE);
          availabilityRepository.save(availability);
          // Gửi email thông báo hủy lịch cho coach
          User user = appointment.getUser();
          String userEmail = user.getEmail();
          User coach = appointment.getCoach();
          String coachEmail = coach.getEmail();
          String[] recipientEmail = {userEmail, coachEmail};
          MailBody mailBody =
              MailBody.builder()
                  .subject("Thông báo hủy lịch hẹn tư vấn")
                  .to(recipientEmail)
                  .body(reason)
                  .build();
          emailSenderService.sendEmail(mailBody);
        }
        return appointmentMapper.toResponse(appointment);
      } else {
        throw new RuntimeException("Invalid status for cancellation");
      }
    } else {
      throw new EntityNotFoundException("Appointment not found");
    }
  }

  private Appointment getByUser_UsernameAndAppointmentDateTime(String username, Instant time) {
    return appointmentRepository.findByUser_UsernameAndAppointmentDateTime(username, time);
  }

  public List<AppointmentResponse> getAppointmentsByUserID(String userID) {
    List<Appointment> appointments =
        appointmentRepository.findByUser_UserIDOrderByAppointmentDateTimeAsc(userID);
    return appointments.stream().map(appointmentMapper::toResponse).toList();
  }

  public List<AppointmentResponse> getAppointmentsByCoachID(String coachID) {
    List<Appointment> appointments =
        appointmentRepository.findByCoach_UserIDOrderByAppointmentDateTimeAsc(coachID);
    return appointments.stream().map(appointmentMapper::toResponse).toList();
  }

  public List<AppointmentResponse> getTodayAppointmentsByUserID(String userID) {
    ZoneId zone = ZoneId.systemDefault();
    Instant startOfDay = LocalDate.now().atStartOfDay(zone).toInstant();
    Instant endOfDay = LocalDate.now().plusDays(1).atStartOfDay(zone).toInstant();
    List<Appointment> appointments =
        appointmentRepository
            .findByUser_UserIDAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeDesc(
                userID, startOfDay, endOfDay);
    return appointments.stream().map(appointmentMapper::toResponse).toList();
  }

  public List<AppointmentResponse> getTodayAppointmentsByCoachID(String coachID) {
    ZoneId zone = ZoneId.systemDefault();
    Instant startOfDay = LocalDate.now().atStartOfDay(zone).toInstant();
    Instant endOfDay = LocalDate.now().plusDays(1).atStartOfDay(zone).toInstant();
    List<Appointment> appointments =
        appointmentRepository
            .findByCoach_UserIDAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeDesc(
                coachID, startOfDay, endOfDay);
    return appointments.stream().map(appointmentMapper::toResponse).toList();
  }
}
