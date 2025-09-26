package com.devteria.identity_service.response;

import com.devteria.identity_service.enums.AppointmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailabilityResponse {
    String availabilityID;
    AppointmentStatus status;
    List<String> appointmentDateTime;
    String createdAt;
    UserResponse coach;
}
