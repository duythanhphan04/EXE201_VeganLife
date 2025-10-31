package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.ApiResponse;
import com.devteria.identity_service.dto.CreateAppointmentRequest;
import com.devteria.identity_service.dto.UpdateAppointmentRequest;
import com.devteria.identity_service.enums.AppointmentStatus;
import com.devteria.identity_service.response.AppointmentResponse;
import com.devteria.identity_service.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {
  private final AppointmentService appointmentService;

  @PostMapping
  @Operation(summary = "Create a new appointment")
  public ApiResponse<AppointmentResponse> createAppointment(
          
      @Valid @RequestBody CreateAppointmentRequest request) throws IOException {
    AppointmentResponse appointmentResponse = appointmentService.createAppointment(request);
    return ApiResponse.<AppointmentResponse>builder()
        .code(1000)
        .message("Appointment created successfully")
        .data(appointmentResponse)
        .build();
  }

  @GetMapping
  @Operation(summary = "Get all appointments")
  public ApiResponse<List<AppointmentResponse>> getAllAppointments() {
    List<AppointmentResponse> appointmentResponses = appointmentService.getAllAppointments();
    return ApiResponse.<List<AppointmentResponse>>builder()
        .code(1000)
        .message("Appointments retrieved successfully")
        .data(appointmentResponses)
        .build();
  }

  @GetMapping("/my-appointments/{userID}")
  @Operation(summary = "Get appointments by user ID")
  public ApiResponse<List<AppointmentResponse>> getUserAppointmentsByID(
      @PathVariable String userID) {
    List<AppointmentResponse> appointmentResponses =
        appointmentService.getAppointmentsByUserID(userID);
    return ApiResponse.<List<AppointmentResponse>>builder()
        .code(1000)
        .message("Appointments retrieved successfully")
        .data(appointmentResponses)
        .build();
  }

  @GetMapping("/coach-appointments/{coachID}")
  @Operation(summary = "Get appointments by coach ID")
  public ApiResponse<List<AppointmentResponse>> getCoachAppointmentsByID(
      @PathVariable String coachID) {
    List<AppointmentResponse> appointmentResponses =
        appointmentService.getAppointmentsByCoachID(coachID);
    return ApiResponse.<List<AppointmentResponse>>builder()
        .code(1000)
        .message("Appointments retrieved successfully")
        .data(appointmentResponses)
        .build();
  }

  @GetMapping("/{appointmentID}")
  @Operation(summary = "Get appointment by appointment ID")
  public ApiResponse<AppointmentResponse> getAppointmentByID(@PathVariable String appointmentID) {
    AppointmentResponse appointmentResponse =
        appointmentService.getAppointmentResponseById(appointmentID);
    return ApiResponse.<AppointmentResponse>builder()
        .code(1000)
        .message("Appointment retrieved successfully")
        .data(appointmentResponse)
        .build();
  }

  @PutMapping("/{appointmentID}")
  @Operation(summary = "Update appointment by appointment ID")
  public ApiResponse<AppointmentResponse> updateAppointmentByID(
      @PathVariable String appointmentID, @Valid @RequestBody UpdateAppointmentRequest request)
      throws IOException {
    AppointmentResponse appointmentResponse =
        appointmentService.updateAppointment(appointmentID, request);
    return ApiResponse.<AppointmentResponse>builder()
        .code(1000)
        .message("Appointment updated successfully")
        .data(appointmentResponse)
        .build();
  }

  @GetMapping("/today-appointments/user/{userID}")
  @Operation(summary = "Get today's appointments by user ID")
  public ApiResponse<List<AppointmentResponse>> getTodayAppointmentsByUserID(
      @PathVariable String userID) {
    List<AppointmentResponse> appointmentResponses =
        appointmentService.getTodayAppointmentsByUserID(userID);
    return ApiResponse.<List<AppointmentResponse>>builder()
        .code(1000)
        .message("Today's appointments retrieved successfully")
        .data(appointmentResponses)
        .build();
  }

  @GetMapping("/today-appointments/coach/{coachID}")
  @Operation(summary = "Get today's appointments by coach ID")
  public ApiResponse<List<AppointmentResponse>> getTodayAppointmentsByCoachID(
      @PathVariable String coachID) {
    List<AppointmentResponse> appointmentResponses =
        appointmentService.getTodayAppointmentsByCoachID(coachID);
    return ApiResponse.<List<AppointmentResponse>>builder()
        .code(1000)
        .message("Today's appointments retrieved successfully")
        .data(appointmentResponses)
        .build();
  }

  @GetMapping("/appointments/status/{status}")
  @Operation(summary = "Get appointments by status")
  public ApiResponse<List<LocalDateTime>> getUserBookedSlotsByStatus(
      String username, String from, String to, @PathVariable AppointmentStatus status) {
    List<LocalDateTime> appointmentResponses =
        appointmentService.getUserBookedAppointmentByStatus(username, status, from, to);
    return ApiResponse.<List<LocalDateTime>>builder()
        .code(1000)
        .message("Appointments retrieved successfully")
        .data(appointmentResponses)
        .build();
  }

  @DeleteMapping("/{appointmentID}/google/{googleAccessToken}")
    public ApiResponse<Void> deleteAppointmentByID(@PathVariable String appointmentID, @PathVariable String googleAccessToken) {
      try{
          appointmentService.deleteAppointment(appointmentID,googleAccessToken);
          return ApiResponse.<Void>builder()
                  .message("Appointment deleted successfully")
                  .code(1000)
                  .build();
      } catch (GeneralSecurityException | IOException e) {
          return ApiResponse.<Void>builder()
                  .message("Appointment deletion failed")
                  .code(1000)
                  .build();
      }
  }
}
