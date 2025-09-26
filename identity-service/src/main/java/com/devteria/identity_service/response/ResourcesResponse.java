package com.devteria.identity_service.response;

import com.devteria.identity_service.enums.ResourcesStatus;
import com.devteria.identity_service.enums.ResourcesType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResourcesResponse {
    String resourcesID;
    String resourcesName;
    String img;
    String description;
    String content;
    Integer readingTime;
    ResourcesType resourcesType;
    ResourcesStatus resourcesStatus;
    Instant createdAt;
    UserResponse user;
}
