package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.ApiResponse;
import com.devteria.identity_service.dto.CreateAppointmentRequest;
import com.devteria.identity_service.entity.Appointment;
import com.devteria.identity_service.response.AppointmentResponse;
import com.devteria.identity_service.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;
    @PostMapping
    public ApiResponse<AppointmentResponse> createAppointment(@Valid @RequestBody CreateAppointmentRequest request) throws IOException {
        AppointmentResponse appointmentResponse = appointmentService.createAppointment(request);
        return ApiResponse.<AppointmentResponse>builder()
                .code(1000)
                .message("Appointment created successfully")
                .data(appointmentResponse)
                .build();
    }
}
