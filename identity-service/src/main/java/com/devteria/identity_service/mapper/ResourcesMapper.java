package com.devteria.identity_service.mapper;

import com.devteria.identity_service.dto.CreateResourcesRequest;
import com.devteria.identity_service.entity.Resources;
import com.devteria.identity_service.response.ResourcesResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",uses = { UserMapper.class })
public interface ResourcesMapper {
    Resources toResources(CreateResourcesRequest resources);

    ResourcesResponse toResourcesResponse(Resources resources);
}
