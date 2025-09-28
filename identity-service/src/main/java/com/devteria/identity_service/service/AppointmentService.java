package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.CreateAppointmentRequest;
import com.devteria.identity_service.entity.Appointment;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.AppointmentStatus;
import com.devteria.identity_service.mapper.AppointmentMapper;
import com.devteria.identity_service.repository.AppointmentRepository;
import com.devteria.identity_service.repository.AvailabilityRepository;
import com.devteria.identity_service.response.AppointmentResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentService {
  private final AppointmentRepository appointmentRepository;
  private final AppointmentMapper appointmentMapper;
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
}
