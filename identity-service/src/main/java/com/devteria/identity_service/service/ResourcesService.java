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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourcesService {
    private static final int WORDS_PER_MINUTE = 200;
    private final ResourcesRepository resourcesRepository;
    private final ResourcesMapper resourcesMapper;
    private final UserService userService;
    private final UserRepository userRepository;

    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResourcesResponse createResources (CreateResourcesRequest request) {
        Resources resources = resourcesMapper.toResources(request);
        String loginUsername = userService.getLoggedInUsername();
        User loginUser = userService.getUserEntity(loginUsername);
        resources.setUser(loginUser);
        resources.setReadingTime(calculateReadingTime(request.getContent()));
        resources.setCreatedAt(Instant.now());
        resourcesRepository.save(resources);
        return resourcesMapper.toResourcesResponse(resources);
    }

    public Integer calculateReadingTime(String content) {
        if(content==null || content.isEmpty())
            return 0;
        int wordCount = content.trim().split("\\s+").length;
        return (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
    }
    public List<ResourcesResponse> getAllResources() {
        List<Resources> resources = resourcesRepository.findAll();
        return resources.stream()
                .map(resourcesMapper::toResourcesResponse)
                .toList();
    }

    public ResourcesResponse getResourcesById(String resourceId) {
        Resources resources = resourcesRepository.findResourceByResourcesID(resourceId);
        return resourcesMapper.toResourcesResponse(resources);
    }

    public List<ResourcesResponse> getResourcesByStatus (ResourcesStatus resourcesStatus) {
        List<Resources> resources = resourcesRepository.findResourcesByResourcesStatus(resourcesStatus);
        return  resources.stream()
                .map(resourcesMapper::toResourcesResponse)
                .toList();
    }

    public ResourcesResponse updateResources(String resourceId, @Valid UpdateResourcesRequest request) {
        Resources resources = resourcesRepository.findResourceByResourcesID(resourceId);
        if(resources.getUser()!=null && resources.getUser().getUserID()!=null){
            User user = userRepository.findById(resources.getUser().getUserID()).get();
            resources.setUser(user);
        }
        resources.setResourcesName(request.getResourceName());
        resources.setImg(request.getImage());
        resources.setDescription(request.getDescription());
        resources.setContent(request.getContent());
        resources.setResourcesType(request.getResourcesType());
        if(!request.getResourcesStatus().equals(ResourcesStatus.DRAFT)){
            resources.setResourcesStatus(ResourcesStatus.PENDING);
        }
        resourcesRepository.save(resources);
        return resourcesMapper.toResourcesResponse(resources);
    }
    public ResourcesResponse deleteResources(String resourceId) {
        Resources resources = resourcesRepository.findResourceByResourcesID(resourceId);
        if(resources!=null){
            resourcesRepository.delete(resources);
        }
        return resourcesMapper.toResourcesResponse(resources);
    }

    public List<ResourcesResponse> getResourcesByType(ResourcesType type) {
        List<Resources> resources = resourcesRepository.findResourcesByResourcesType(type);
        return resources.stream()
                .map(resourcesMapper::toResourcesResponse)
                .toList();
    }
}
