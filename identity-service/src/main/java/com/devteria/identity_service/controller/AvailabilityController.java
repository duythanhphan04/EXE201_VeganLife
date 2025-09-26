//package com.devteria.identity_service.controller;
//
//import com.devteria.identity_service.dto.ApiResponse;
//import com.devteria.identity_service.dto.CreateAvailabilityRequest;
//import com.devteria.identity_service.response.AvailabilityResponse;
//import com.devteria.identity_service.service.AvailabilityService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/availability")
//@Tag(name = "Availability", description = "Endpoints for managing coach availabilities")
//@RequiredArgsConstructor
//public class AvailabilityController {
//    private final AvailabilityService availabilityService;
//    @PostMapping
//    @Operation(summary = "Create coach availabilities", description = "Create multiple availability slots for the logged-in coach.")
//    public ApiResponse<List<AvailabilityResponse>> getAvailability( @Valid @RequestBody CreateAvailabilityRequest request){
//        List<AvailabilityResponse> responses = availabilityService.createCoachAvailabilities(request);
//        return ApiResponse.<List<AvailabilityResponse>>builder()
//                .code(2001)
//                .data(responses)
//                .build();
//    }
//}
