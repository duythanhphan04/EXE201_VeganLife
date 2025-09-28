package com.devteria.identity_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
  USER_NOT_FOUND(1001, "User not found", HttpStatus.BAD_REQUEST),
  COACH_NOT_FOUND(1013, "Coach not found", HttpStatus.BAD_REQUEST),
  USERNAME_ALREADY_EXISTS(1002, "Username already exists", HttpStatus.NOT_FOUND),
  INVALID_PASSWORD(1003, "Invalid password", HttpStatus.BAD_REQUEST),
  INVALID_FIRST_NAME(1004, "Invalid first name", HttpStatus.BAD_REQUEST),
  INVALID_LAST_NAME(1005, "Invalid last name", HttpStatus.BAD_REQUEST),
  INVALID_DOB(1006, "Invalid date of birth", HttpStatus.BAD_REQUEST),
  INVALID_USERNAME(1007, " Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
  INVALID_PASSWORD_LENGTH(1008, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
  INVALID_KEY(1009, "Invalid message key", HttpStatus.BAD_REQUEST),
  UNAUTHENTICATED(1010, "Unauthenticated", HttpStatus.UNAUTHORIZED),
  UNCATEGORIZED(1011, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
  WRONG_CREDENTIALS(1012, "Incorrect Username or Password", HttpStatus.FORBIDDEN),
  BLOCKED_USER(1014, "User is blocked", HttpStatus.FORBIDDEN),
  UNAUTHORIZED(1015, "Unauthorized: Access is denied", HttpStatus.FORBIDDEN),
  INVALID_ROLE(1016, "Invalid role", HttpStatus.FORBIDDEN);
  private final int code;
  private final String message;
  private final HttpStatusCode httpStatusCode;

  ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
    this.code = code;
    this.message = message;
    this.httpStatusCode = httpStatusCode;
  }
}
