package com.devteria.identity_service.mapper;

import com.devteria.identity_service.entity.Availability;
import com.devteria.identity_service.response.AvailabilityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {
    @Mapping(target = "availabilityDatetime", expression = "java(java.util.List.of(availability.getAvailabilityDatetime().toString()))")
    AvailabilityResponse toAvailabilityResponse(Availability availability);
}
