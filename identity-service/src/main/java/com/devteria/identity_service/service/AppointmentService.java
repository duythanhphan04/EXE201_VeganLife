package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.CreateAppointmentRequest;
import com.devteria.identity_service.dto.UpdateAppointmentRequest;
import com.devteria.identity_service.entity.Appointment;

import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.AppointmentStatus;
import com.devteria.identity_service.mapper.AppointmentMapper;
import com.devteria.identity_service.repository.AppointmentRepository;
import com.devteria.identity_service.repository.AvailabilityRepository;
import com.devteria.identity_service.response.AppointmentResponse;
import com.google.api.services.calendar.model.Event;
import java.io.IOException;
import java.security.GeneralSecurityException;
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
    Event eventGG =
        googleCalendarService.createGGMeetAppointment(
            request,
            user.getEmail(),
            request.getGoogleAccessToken() // cần có field googleAccessToken trong request
            );

    // Tạo entity Appointment
    Appointment appointment = appointmentMapper.toEntity(request);
    appointment.setLink(eventGG.getHangoutLink());
    appointment.setGoogleEventId(eventGG.getId());
    Instant appointmentDateTime = Instant.parse(request.getAppointmentDateTime());
    appointment.setAppointmentDateTime(appointmentDateTime);
    appointment.setStatus(AppointmentStatus.AVAILABLE);
    appointment.setUser(user);

    // Set coach
    User coach = userService.getUserEntityByID(request.getCoachID());
    String coachUsername = coach.getUsername();
    appointment.setCoach(coach);

    // Lưu DB
    appointmentRepository.save(appointment);

    // Xác nhận slot của coach đã được book
    availabilityService.confirmConsultantScheduledSlot(
        coachUsername, appointmentDateTime, AppointmentStatus.UNAVAILABLE);

    return appointmentMapper.toResponse(appointment);
  }

  public List<AppointmentResponse> getAllAppointments() {
    List<Appointment> appointments = appointmentRepository.findAll();
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
  public void deleteAppointment(String appointmentID, String googleAccessToken) throws GeneralSecurityException, IOException {
      Appointment appointment = appointmentRepository.findById(appointmentID).orElse(null);
      if(appointment != null) {
          googleCalendarService.deleteEvent(appointment.getGoogleEventId(),googleAccessToken);
      }
      appointmentRepository.delete(appointment);
      availabilityService.confirmConsultantScheduledSlot(appointment.getCoach().getUsername(),appointment.getAppointmentDateTime(),AppointmentStatus.AVAILABLE);
  }
}
