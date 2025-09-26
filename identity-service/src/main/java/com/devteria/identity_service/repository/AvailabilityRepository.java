package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
@Repository
public interface AvailabilityRepository extends JpaRepository<Availability,String> {
    List<Availability> findByCoach_UsernameAndAvailabilityDatetimeBetween(String username, Instant from, Instant to);
    Availability findByCoach_UsernameAndAvailabilityDatetime(String username, Instant availabilityDateTime);
}
