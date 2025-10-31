package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.CreateResourcesRequest;
import com.devteria.identity_service.dto.UpdateResourcesRequest;
import com.devteria.identity_service.entity.Resources;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.ResourcesStatus;
import com.devteria.identity_service.enums.ResourcesType;
import com.devteria.identity_service.mapper.ResourcesMapper;
import com.devteria.identity_service.repository.ResourcesRepository;
import com.devteria.identity_service.repository.UserRepository;
import com.devteria.identity_service.response.ResourcesResponse;
import jakarta.validation.Valid;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourcesService {
    private static final int WORDS_PER_MINUTE = 200;
    private final ResourcesRepository resourcesRepository;
    @Autowired
    private final ResourcesMapper resourcesMapper;
    private final UserService userService;
    private final UserRepository userRepository;

    public ResourcesResponse createResources(CreateResourcesRequest request) {
        Resources resources = new Resources();
        resources.setResourcesName(request.getResourcesName());
        resources.setImg(request.getImg());
        resources.setDescription(request.getDescription());
        resources.setContent(request.getContent());
        resources.setResourcesType(request.getResourcesType());
        resources.setResourcesStatus(request.getResourcesStatus());
        String loginUsername = userService.getLoggedInUsername();
        User loginUser = userService.getUserEntity(loginUsername);
        resources.setUser(loginUser);
        resources.setReadingTime(calculateReadingTime(request.getContent()));
        resources.setCreatedAt(Instant.now());
        resourcesRepository.save(resources);
        ResourcesResponse resourcesResponse = new ResourcesResponse();
        resourcesResponse.setResourcesID(resources.getResourcesID());
        resourcesResponse.setResourcesName(resources.getResourcesName());
        resourcesResponse.setImg(resources.getImg());
        resourcesResponse.setDescription(resources.getDescription());
        resourcesResponse.setContent(resources.getContent());
        resourcesResponse.setResourcesType(resources.getResourcesType());
        resourcesResponse.setResourcesStatus(resources.getResourcesStatus());
        resourcesResponse.setReadingTime(resources.getReadingTime());
        resourcesResponse.setCreatedAt(resources.getCreatedAt());
        return resourcesResponse;
    }

    public Integer calculateReadingTime(String content) {
        if (content == null || content.isEmpty()) return 0;
        int wordCount = content.trim().split("\\s+").length;
        return (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
    }

    public List<ResourcesResponse> getAllResources() {
        List<Resources> resources = resourcesRepository.findAll();
        return resources.stream().map(resources1 -> {
            ResourcesResponse resp = new ResourcesResponse();
            resp.setResourcesID(resources1.getResourcesID());
            resp.setResourcesName(resources1.getResourcesName());
            resp.setImg(resources1.getImg());
            resp.setDescription(resources1.getDescription());
            resp.setContent(resources1.getContent());
            resp.setResourcesType(resources1.getResourcesType());
            resp.setResourcesStatus(resources1.getResourcesStatus());
            resp.setReadingTime(resources1.getReadingTime());
            resp.setCreatedAt(resources1.getCreatedAt());
            return resp;
        }).toList();
    }

    public ResourcesResponse getResourcesById(String resourceId) {
        Resources resources = resourcesRepository.findResourceByResourcesID(resourceId);
        ResourcesResponse response = new ResourcesResponse();
        response.setResourcesID(resources.getResourcesID());
        response.setResourcesName(resources.getResourcesName());
        response.setImg(resources.getImg());
        response.setDescription(resources.getDescription());
        response.setContent(resources.getContent());
        return response;
    }

    public List<ResourcesResponse> getResourcesByStatus(ResourcesStatus resourcesStatus) {
        List<Resources> resources = resourcesRepository.findResourcesByResourcesStatus(resourcesStatus);
        return resources.stream().map(r -> {
            ResourcesResponse resp = new ResourcesResponse();
            resp.setResourcesID(r.getResourcesID());
            resp.setResourcesName(r.getResourcesName());
            resp.setImg(r.getImg());
            resp.setDescription(r.getDescription());
            resp.setContent(r.getContent());
            resp.setResourcesType(r.getResourcesType());
            resp.setResourcesStatus(r.getResourcesStatus());
            resp.setReadingTime(r.getReadingTime());
            resp.setCreatedAt(r.getCreatedAt());
            return resp;
        }).toList();
    }

    public ResourcesResponse updateResources(
            String resourceId, @Valid UpdateResourcesRequest request) {
        Resources resources = resourcesRepository.findResourceByResourcesID(resourceId);
        if (resources.getUser() != null && resources.getUser().getUserID() != null) {
            User user = userRepository.findById(resources.getUser().getUserID()).get();
            resources.setUser(user);
        }
        resources.setResourcesName(request.getResourceName());
        resources.setImg(request.getImage());
        resources.setDescription(request.getDescription());
        resources.setContent(request.getContent());
        resources.setResourcesType(request.getResourcesType());
        if (!request.getResourcesStatus().equals(ResourcesStatus.DRAFT)) {
            resources.setResourcesStatus(ResourcesStatus.PENDING);
        }
        resourcesRepository.save(resources);
        ResourcesResponse response = new ResourcesResponse();
        response.setResourcesID(resources.getResourcesID());
        response.setResourcesName(resources.getResourcesName());
        response.setImg(resources.getImg());
        response.setDescription(resources.getDescription());
        response.setContent(resources.getContent());
        response.setResourcesType(resources.getResourcesType());
        response.setResourcesStatus(resources.getResourcesStatus());
        response.setReadingTime(resources.getReadingTime());
        response.setCreatedAt(resources.getCreatedAt());
        return response;
    }

    public ResourcesResponse deleteResources(String resourceId) {
        Resources resources = resourcesRepository.findResourceByResourcesID(resourceId);
        if (resources != null) {
            resourcesRepository.delete(resources);
        }
        ResourcesResponse response = new ResourcesResponse();
        response.setResourcesID(resources.getResourcesID());
        response.setResourcesName(resources.getResourcesName());
        response.setImg(resources.getImg());
        response.setDescription(resources.getDescription());
        response.setContent(resources.getContent());
        response.setResourcesType(resources.getResourcesType());
        response.setResourcesStatus(resources.getResourcesStatus());
        response.setReadingTime(resources.getReadingTime());
        response.setCreatedAt(resources.getCreatedAt());
        return response;
    }

    public List<ResourcesResponse> getResourcesByType(ResourcesType type) {
        List<Resources> resources = resourcesRepository.findResourcesByResourcesType(type);
        return resources.stream().map(resources1 -> {
            ResourcesResponse resp = new ResourcesResponse();
            resp.setResourcesID(resources1.getResourcesID());
            resp.setResourcesName(resources1.getResourcesName());
            resp.setImg(resources1.getImg());
            resp.setDescription(resources1.getDescription());
            resp.setContent(resources1.getContent());
            resp.setResourcesType(resources1.getResourcesType());
            resp.setResourcesStatus(resources1.getResourcesStatus());
            resp.setReadingTime(resources1.getReadingTime());
            resp.setCreatedAt(resources1.getCreatedAt());
            return resp;
        }).toList();
    }
}
