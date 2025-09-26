//package com.devteria.identity_service.service;
//
//import com.devteria.identity_service.dto.CreateAppointmentRequest;
//import com.devteria.identity_service.entity.Appointment;
//import com.devteria.identity_service.entity.User;
//import com.devteria.identity_service.enums.AppointmentStatus;
//import com.devteria.identity_service.mapper.AppointmentMapper;
//import com.devteria.identity_service.repository.AppointmentRepository;
//import com.devteria.identity_service.repository.AvailabilityRepository;
//import com.devteria.identity_service.response.AppointmentResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.time.Instant;
//import java.time.ZoneId;
//
//@Service
//@RequiredArgsConstructor
//public class AppointmentService {
//    private final AppointmentRepository appointmentRepository;
//    private final AppointmentMapper appointmentMapper;
//    private final UserService userService;
//    private final AvailabilityService availabilityService;
//    private final AvailabilityRepository availabilityRepository;
//    private final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
//    private final EmailSenderService emailSenderService;
//    private final GoogleCalendarService googleCalendarService;
//    public AppointmentResponse createAppointment(CreateAppointmentRequest request) throws IOException {
//        Appointment appointment = appointmentMapper.toEntity(request);
//        String link =googleCalendarService.createGGMeetAppointment(request);
//        appointment.setLink(link);
//        Instant appointmentDateTime = Instant.parse(request.getAppointmentDateTime());
//        appointment.setAppointmentDateTime(appointmentDateTime);
//        appointment.setStatus(AppointmentStatus.SCHEDULED);
//        String loginUsername = userService.getLoggedInUsername();
//        User user = userService.getUserEntity(loginUsername);
//        appointment.setUser(user);
//        String coachUsername = request.getCoachID();
//        User coach = userService.getUserEntity(coachUsername);
//        appointment.setCoach(coach);
//        appointmentRepository.save(appointment);
//        availabilityService.confirmConsultantScheduledSlot(coachUsername, appointmentDateTime, AppointmentStatus.CONFIRMED);
//        return appointmentMapper.toResponse(appointment);
//
//    }
//}
