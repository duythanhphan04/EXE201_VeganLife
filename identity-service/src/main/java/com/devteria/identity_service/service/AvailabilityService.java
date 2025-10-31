package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.CreateAvailabilityRequest;
import com.devteria.identity_service.entity.Availability;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.AppointmentStatus;
import com.devteria.identity_service.mapper.AvailabilityMapper;
import com.devteria.identity_service.repository.AppointmentRepository;
import com.devteria.identity_service.repository.AvailabilityRepository;
import com.devteria.identity_service.response.AvailabilityResponse;
import com.devteria.identity_service.response.UserResponse;
import jakarta.transaction.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final UserService userService;
    @Autowired
    private final AvailabilityMapper availabilityMapper;
    private final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private final EmailSenderService emailSenderService;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public List<AvailabilityResponse> createCoachAvailabilities(CreateAvailabilityRequest request) {
        String loginCoach = userService.getLoggedInUsername();
        User coach = userService.getUserEntity(loginCoach);
        List<String> requestedDateTimesString = request.getAvailabilityDatetime();
        List<Instant> requestedAvailabilityTimes =
                requestedDateTimesString.stream().map(Instant::parse).toList();
        Set<Instant> existingTimesForCoach =
                coach.getAvailabilities().stream()
                        .map(Availability::getAvailabilityDatetime)
                        .collect(Collectors.toSet());
        List<Availability> newAvailabilitiesToPersist = new ArrayList<>();
        for (Instant time : requestedAvailabilityTimes) {
            if (existingTimesForCoach.contains(time)) {
                System.out.println(
                        "Availability at " + time + " already exists for coach " + coach.getUsername());
                continue;
            }
            Availability availability =
                    Availability.builder()
                            .coach(coach)
                            .availabilityDatetime(time)
                            .status(AppointmentStatus.AVAILABLE)
                            .build();
            newAvailabilitiesToPersist.add(availability);
        }
        List<Availability> savedAvailabilities =
                availabilityRepository.saveAll(newAvailabilitiesToPersist);
        coach.getAvailabilities().addAll(newAvailabilitiesToPersist);
        return savedAvailabilities.stream()
                .map(availability -> {
                    AvailabilityResponse availabilityResponse = new AvailabilityResponse();
                    availabilityResponse.setAvailabilityID(availability.getAvailabilityID());
                    availabilityResponse.setCoach(UserResponse.builder()
                            .userID(coach.getUserID())
                            .username(coach.getUsername())
                            .email(coach.getEmail())
                            .fullName(coach.getFullName())
                            .build());
                    availabilityResponse.setAvailabilityDatetime(
                            availabilityMapper.mapInstantToList(availability.getAvailabilityDatetime()));
                    availabilityResponse.setStatus(availability.getStatus());
                    return availabilityResponse;
                })
                .collect(Collectors.toList());
    }

    public List<Availability> getCoachBookedSlotsByStatus(
            String userID, AppointmentStatus status) {
        return availabilityRepository.getAvailabilitiesByCoach_UserIDAndStatus(userID, status);
    }

    public List<Availability> getByCoachUsernameAndAvailabilityDateTimeBetween(
            String username, Instant from, Instant to) {
        return availabilityRepository.findByCoach_UsernameAndAvailabilityDatetimeBetween(
                username, from, to);
    }

    public Availability getCoachAvailabilityEntityByAvailabilityDateTime(
            String username, Instant availabilityDateTime) {
        return availabilityRepository.findByCoach_UsernameAndAvailabilityDatetime(
                username, availabilityDateTime);
    }

    public List<Availability> getCoachAvailableSlots(String userID) {
        return availabilityRepository.findByCoach_UserID(userID);
    }

    public void confirmConsultantScheduledSlot(
            String coachUsername, Instant appointmentDateTime, AppointmentStatus appointmentStatus) {
        Availability availability =
                getCoachAvailabilityEntityByAvailabilityDateTime(coachUsername, appointmentDateTime);
        availability.setStatus(appointmentStatus);
        availabilityRepository.save(availability);
    }

    public Availability deleteAvailability(String coachID, String availabilityID) {
        Availability availability = availabilityRepository.findById(availabilityID).orElse(null);
        if (availability == null || !availability.getCoach().getUserID().equals(coachID)) {
            throw new IllegalArgumentException("Availability not found or coach mismatch");
        }
        availabilityRepository.delete(availability);
        return availability;
    }
}
