package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.ApiResponse;
import com.devteria.identity_service.dto.CreateResourcesRequest;
import com.devteria.identity_service.dto.UpdateResourcesRequest;
import com.devteria.identity_service.enums.ResourcesStatus;
import com.devteria.identity_service.enums.ResourcesType;
import com.devteria.identity_service.response.ResourcesResponse;
import com.devteria.identity_service.service.ResourcesService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourcesController {
  private final ResourcesService resourcesService;

  @PostMapping
  @Operation(summary = "Create new resources")
  public ApiResponse<ResourcesResponse> createResources(
      @Valid @RequestBody CreateResourcesRequest request) {
    ResourcesResponse response = resourcesService.createResources(request);
    return ApiResponse.<ResourcesResponse>builder()
        .data(response)
        .message("Resources created successfully")
        .code(1000)
        .build();
  }

  @GetMapping("/{resourceId}")
  @Operation(summary = "Get resources by ID")
  public ApiResponse<ResourcesResponse> getResourcesById(@PathVariable String resourceId) {
    ResourcesResponse response = resourcesService.getResourcesById(resourceId);
    return ApiResponse.<ResourcesResponse>builder()
        .data(response)
        .message("Resources fetched successfully")
        .code(1000)
        .build();
  }

  @GetMapping
  @Operation(summary = "Get all resources")
  public ApiResponse<List<ResourcesResponse>> getAllResources() {
    List<ResourcesResponse> responses = resourcesService.getAllResources();
    return ApiResponse.<List<ResourcesResponse>>builder()
        .data(responses)
        .message("Resources fetched successfully")
        .code(1000)
        .build();
  }

  @GetMapping("/status/{status}")
  @Operation(summary = "Get resource by status")
  public ApiResponse<List<ResourcesResponse>> getResourcesByStatus(
      @PathVariable ResourcesStatus status) {
    List<ResourcesResponse> responses = resourcesService.getResourcesByStatus(status);
    return ApiResponse.<List<ResourcesResponse>>builder()
        .data(responses)
        .message("Resources fetched successfully")
        .code(1000)
        .build();
  }

  @PutMapping("/{resourceId}")
  @Operation(summary = "Update resources by ID")
  public ApiResponse<ResourcesResponse> updateResources(
      @PathVariable String resourceId, @Valid @RequestBody UpdateResourcesRequest request) {
    ResourcesResponse response = resourcesService.updateResources(resourceId, request);
    return ApiResponse.<ResourcesResponse>builder()
        .data(response)
        .message("Resources updated successfully")
        .code(1000)
        .build();
  }

  @DeleteMapping("/{resourceId}")
  @Operation(summary = "Delete resource by ID")
  public ApiResponse<ResourcesResponse> deleteResources(@PathVariable String resourceId) {
    return ApiResponse.<ResourcesResponse>builder()
        .data(resourcesService.deleteResources(resourceId))
        .message("Resources deleted successfully")
        .build();
  }

  @GetMapping("/type/{type}")
  @Operation(summary = "Get resource by type")
  public ApiResponse<List<ResourcesResponse>> getResourcesByType(@PathVariable ResourcesType type) {
    return ApiResponse.<List<ResourcesResponse>>builder()
        .data(resourcesService.getResourcesByType(type))
        .code(1000)
        .build();
  }
}
