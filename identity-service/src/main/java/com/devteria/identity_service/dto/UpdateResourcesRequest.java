package com.devteria.identity_service.dto;

import com.devteria.identity_service.enums.ResourcesStatus;
import com.devteria.identity_service.enums.ResourcesType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateResourcesRequest {
    @NotBlank(message = "Resource name is required")
    String resourceName;
    String image;
    @NotBlank(message = "Description is required")
    String description;
    @NotBlank(message = "Content is required")
    String content;
    ResourcesType resourcesType;
    ResourcesStatus resourcesStatus;
}
