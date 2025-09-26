package com.devteria.identity_service.response;
import com.devteria.identity_service.entity.PersonalGoal;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.Role;
import com.devteria.identity_service.enums.UserPlan;
import com.devteria.identity_service.enums.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String username;
    String email;
    String fullName;
    String img;
    PersonalGoal personalGoal;
    User coach;
    UserPlan plan;
    String createdAt;
    Role role;
    UserStatus status;
}
