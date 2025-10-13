package com.devteria.identity_service.mapper;

import com.devteria.identity_service.entity.Availability;
import com.devteria.identity_service.response.AvailabilityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring",uses = {UserMapper.class})
public interface AvailabilityMapper {
    @Mapping(target = "availabilityDatetime",
            expression = "java(mapInstantToList(availability.getAvailabilityDatetime()))")
    AvailabilityResponse toAvailabilityResponse(Availability availability);
    default List<String> mapInstantToList(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Collections.singletonList(instant.toString());
    }
}