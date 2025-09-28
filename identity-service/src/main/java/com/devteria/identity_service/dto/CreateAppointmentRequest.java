package com.devteria.identity_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAppointmentRequest {
  String notes;

  @NotNull(message = "Appointment date and time is required")
  String appointmentDateTime;

  @NotNull(message = "Coach ID (username) is required")
  String coachID;

  String googleAccessToken;
}
