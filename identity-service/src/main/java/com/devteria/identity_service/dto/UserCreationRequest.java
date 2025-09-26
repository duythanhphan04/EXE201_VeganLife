package com.devteria.identity_service.dto;

import com.devteria.identity_service.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, message = "INVALID_USERNAME")
     String username;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "INVALID_PASSWORD_LENGTH")
     String password;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "INVALID_PASSWORD_LENGTH")
    String fullName;
}
