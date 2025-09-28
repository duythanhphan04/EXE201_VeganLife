package com.devteria.identity_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateAvailabilityRequest {
  @NotNull(message = "Availability date and time is required")
  String availabilityDateTime;

  @NotNull(message = "Reason is required")
  String reason;
}
