package com.devteria.identity_service.response;

import com.devteria.identity_service.enums.AppointmentStatus;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailabilityResponse {
  String availabilityID;
  AppointmentStatus status;
  String availabilityDatetime ;
  String createdAt;
  UserResponse coach;
}
