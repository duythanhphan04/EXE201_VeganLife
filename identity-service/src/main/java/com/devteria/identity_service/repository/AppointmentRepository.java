package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,String> {
    Appointment findByCoachUsernameAndAppointmentDateTime(String coachUsername, Instant appointmentDateTime);
}
