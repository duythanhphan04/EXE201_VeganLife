package com.devteria.identity_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Size(max = 255, message = "Email must be less than 256 characters")
  String email;

  @NotBlank(message = "Full name is required")
  @Size(max = 255, message = "Full name must be less than 256 characters")
  String fullName;

  @Size(max = 300, message = "Image URL must be less than 256 characters")
  String img;
}
