package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.ApiResponse;
import com.devteria.identity_service.dto.CreateAvailabilityRequest;
import com.devteria.identity_service.enums.AppointmentStatus;
import com.devteria.identity_service.response.AvailabilityResponse;
import com.devteria.identity_service.service.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/availability")
@Tag(name = "Availability", description = "Endpoints for managing coach availabilities")
@RequiredArgsConstructor
public class AvailabilityController {
  private final AvailabilityService availabilityService;

  @PostMapping
  @Operation(
      summary = "Create coach availabilities",
      description = "Create multiple availability slots for the logged-in coach.")
  public ApiResponse<List<AvailabilityResponse>> getAvailability(
      @Valid @RequestBody CreateAvailabilityRequest request) {
    List<AvailabilityResponse> responses = availabilityService.createCoachAvailabilities(request);
    return ApiResponse.<List<AvailabilityResponse>>builder().code(1000).data(responses).build();
  }

  @GetMapping("/available-slots")
  @Operation(
      summary = "Get coach available slots",
      description = "Retrieve available slots for a specific coach within a date range.")
  public ApiResponse<List<LocalDateTime>> getCoachAvailableSlots(
      String username, String from, String to) {
    List<LocalDateTime> responses = availabilityService.getCoachAvailableSlots(username, from, to);
    return ApiResponse.<List<LocalDateTime>>builder().code(1000).data(responses).build();
  }

  @GetMapping("/slots/{status}")
  @Operation(
      summary = "Get coach booked slots by status",
      description =
          "Retrieve booked slots for a specific coach within a date range filtered by appointment status.")
  public ApiResponse<List<LocalDateTime>> getCoachBookedSlotsByStatus(
      String username, String from, String to, @PathVariable AppointmentStatus status) {
    List<LocalDateTime> responses =
        availabilityService.getCoachBookedSlotsByStatus(username, from, to, status);
    return ApiResponse.<List<LocalDateTime>>builder().code(1000).data(responses).build();
  }

  @PutMapping("/{status}")
  @Operation(
      summary = "Cancel coach scheduled slots",
      description = "Cancel scheduled slots for the logged-in coach based on appointment status.")
  public ApiResponse<AvailabilityResponse> cancelCoachScheduledSlots(
      @PathVariable AppointmentStatus status, @Valid @RequestBody UpdateAvailabilityRequest request)
      throws MessagingException {
    AvailabilityResponse response = availabilityService.cancelCoachScheduledSlots(status, request);
    return ApiResponse.<AvailabilityResponse>builder().code(1000).data(response).build();
  }
}
