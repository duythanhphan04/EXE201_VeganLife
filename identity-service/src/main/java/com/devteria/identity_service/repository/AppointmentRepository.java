package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.Appointment;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
  Appointment findByCoachUsernameAndAppointmentDateTime(
      String coachUsername, Instant appointmentDateTime);
}
