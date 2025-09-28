package com.devteria.identity_service.mapper;

import com.devteria.identity_service.entity.Availability;
import com.devteria.identity_service.response.AvailabilityResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {
  AvailabilityResponse toAvailabilityResponse(Availability availability);
}
