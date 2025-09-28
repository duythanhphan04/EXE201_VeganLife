package com.devteria.identity_service.dto;

import com.devteria.identity_service.enums.AppointmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateAppointmentRequest {
    @NotBlank(message = "Notes is required")
    String notes;
    @NotNull(message = "Status is required")
    AppointmentStatus status;
    @NotBlank(message = "Appointment date and time is required")
    String appointmentDateTime;
}
