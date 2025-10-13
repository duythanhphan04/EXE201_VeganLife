package com.devteria.identity_service.mapper;

import com.devteria.identity_service.dto.CreateAppointmentRequest;
import com.devteria.identity_service.entity.Appointment;
import com.devteria.identity_service.response.AppointmentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface AppointmentMapper {
    Appointment toEntity(CreateAppointmentRequest request);

    AppointmentResponse toResponse(Appointment appointment);
}

