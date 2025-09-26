package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.ApiResponse;
import com.devteria.identity_service.dto.UserCreationRequest;
import com.devteria.identity_service.dto.UserUpdateRequest;
import com.devteria.identity_service.enums.Role;
import com.devteria.identity_service.enums.UserStatus;
import com.devteria.identity_service.response.UserResponse;
import com.devteria.identity_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "Register a new user")
    public ApiResponse<UserResponse> register(@RequestBody @Valid UserCreationRequest request) {
        UserResponse userResponse = userService.createUser(request);
        return ApiResponse.<UserResponse>builder()
                .data(userResponse)
                .message("User created successfully")
                .code(1000)
                .build();
    }

    @PostMapping("/coach")
    @Operation(summary = "Register a new coach")
    public ApiResponse<UserResponse> registerCoach(@RequestBody @Valid UserCreationRequest request) {
        UserResponse userResponse = userService.createCoach(request);
        return ApiResponse.<UserResponse>builder()
                .data(userResponse)
                .message("Coach created successfully")
                .code(1000)
                .build();
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ApiResponse<List<UserResponse>> getAllUser() {
        return ApiResponse.<List<UserResponse>>builder()
                .data(userService.getAllUser())
                .message("Users fetched successfully")
                .code(1000)
                .build();
    }


    @GetMapping("/{userID}")
    @Operation(summary = "Get user by ID")
    ApiResponse<UserResponse> getUserByID(@PathVariable String userID) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getUserByID(userID))
                .message("User fetched successfully")
                .code(1000)
                .build();
    }

    @PutMapping("/{userID}")
    @Operation(summary = "Update user by ID")
    ApiResponse<UserResponse> update(@PathVariable String userID, @RequestBody UserUpdateRequest userUpdateRequest) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.updateUser(userID, userUpdateRequest))
                .message("User updated successfully")
                .code(1000)
                .build();
    }

    @PutMapping("/soft-delete/{userID}")
    @Operation(summary = "Soft delete user by ID")
    ApiResponse<UserResponse> softDelete(@PathVariable String userID) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.updateUserStatus(userID, UserStatus.INACTIVE))
                .message("User soft-deleted successfully")
                .code(1000)
                .build();
    }

    @DeleteMapping("/{userID}")
    @Operation(summary = "Delete user by ID")
    ApiResponse<UserResponse> delete(@PathVariable String userID) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.deleteUser(userID))
                .message("User deleted successfully")
                .code(1000)
                .build();
    }

    @GetMapping("/my-info")
    @Operation(summary = "Get my user info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getMyInfo())
                .message("User info fetched successfully")
                .code(1000)
                .build();
    }

    @GetMapping("/role/{roleName}")
    @Operation(summary = "Get users by role")
    ApiResponse<List<UserResponse>> getUsersByRole(@PathVariable Role roleName) {
        return ApiResponse.<List<UserResponse>>builder()
                .data(userService.getUserByRole(roleName))
                .message("Users fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("status/{status}")
    @Operation(summary = "Get users by status")
    ApiResponse<List<UserResponse>> getUsersByStatus(@PathVariable UserStatus status) {
        return ApiResponse.<List<UserResponse>>builder()
                .data(userService.getUserByStatus(status))
                .message("Users fetched successfully")
                .code(1002)
                .build();
    }
    @GetMapping("coach_user/{userID}")
    @Operation(summary = "get user's coach by userID")
    ApiResponse<List<UserResponse>> getUserByCoach(@PathVariable String userID) {
        return ApiResponse.<List<UserResponse>>builder()
                .data(userService.getUserByCoach(userID))
                .message("Coach fetched successfully")
                .code(1000)
                .build();
    }
    @PutMapping("/{userID}/coach/{coachID}")
    @Operation(summary = "Assign coach")
    ApiResponse<UserResponse> assignCoach(@PathVariable String userID, @PathVariable String coachID) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.updateUserCoach(userID, coachID))
                .message("Coach assigned successfully")
                .code(1000)
                .build();
    }
}
