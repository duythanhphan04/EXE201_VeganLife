package com.devteria.identity_service.response;

import com.devteria.identity_service.enums.AppointmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentResponse {
    String appointmentID;
    String notes;
    String link;
    AppointmentStatus status;
    String appointmentDateTime;
    String createdAt;
    UserResponse user;
    UserResponse coach;
}
