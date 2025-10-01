package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.ApiResponse;
import com.devteria.identity_service.dto.CreateAvailabilityRequest;
import com.devteria.identity_service.entity.Availability;
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
  public ApiResponse<List<Availability>> getCoachAvailableSlots(String userID) {
    List<Availability> responses = availabilityService.getCoachAvailableSlots(userID);
    return ApiResponse.<List<Availability>>builder().code(1000).data(responses).build();
  }

  @GetMapping("/slots/{status}")
  @Operation(
      summary = "Get coach booked slots by status",
      description =
          "Retrieve booked slots for a specific coach within a date range filtered by appointment status.")
  public ApiResponse<List<Availability>> getCoachBookedSlotsByStatus(
      String userID, @PathVariable AppointmentStatus status) {
    List<Availability> responses =
        availabilityService.getCoachBookedSlotsByStatus(userID, status);
    return ApiResponse.<List<Availability>>builder().code(1000).data(responses).build();
  }

  @DeleteMapping("/delete/{coachID}/availability/{availabilityID}")
    public ApiResponse<Availability> deleteAvailability(@PathVariable String coachID, @PathVariable String availabilityID) {
      Availability availabilityResponse = availabilityService.deleteAvailability(coachID, availabilityID);
      return ApiResponse.<Availability>builder()
              .code(1000).data(availabilityResponse)
              .message("Delete availability successfully")
              .build();
  }
}
