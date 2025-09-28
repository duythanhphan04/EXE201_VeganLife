package com.devteria.identity_service.mapper;

import com.devteria.identity_service.dto.UserCreationRequest;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserResponse toUserResponse(User user);

  User toUser(UserCreationRequest request);
}
