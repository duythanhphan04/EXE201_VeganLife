package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.Appointment;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    Appointment findByCoachUsernameAndAppointmentDateTime(
            String coachUsername, Instant appointmentDateTime);

    List<Appointment> findByUser_UsernameOrderByAppointmentDateTimeAsc(String username);

    List<Appointment> findByCoach_UsernameOrderByAppointmentDateTimeAsc(String coachUsername);

    List<Appointment> findByUser_UsernameAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeDesc(
            String username, Instant startOfDay, Instant endOfDay);

    List<Appointment> findByCoach_UsernameAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeDesc(String coachUsername, Instant startOfDay, Instant endOfDay);

    List<Appointment> findByCreatedAtBetween(Instant start, Instant end);

    long countByCoach_Username(String coachUsername);

    Appointment findByUser_UsernameAndAppointmentDateTime(String loginUsername, Instant appointmentDateTime);
}
