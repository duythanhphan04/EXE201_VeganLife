package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.CreateAppointmentRequest;
import com.devteria.identity_service.entity.Appointment;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.exception.WebException;
import com.devteria.identity_service.repository.AppointmentRepository;
import com.devteria.identity_service.response.AppointmentResponse;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class GoogleCalendarService {

  private final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final AppointmentRepository appointmentRepository;

    public GoogleCalendarService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    private Calendar getCalendarService(String googleAccessToken)
      throws GeneralSecurityException, IOException {
    Credential credential =
        new Credential(BearerToken.authorizationHeaderAccessMethod())
            .setAccessToken(googleAccessToken);

    return new Calendar.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            credential)
        .setApplicationName("Identity Service")
        .build();
  }

    public Event createGGMeetAppointment(
            CreateAppointmentRequest request, String userEmail, String googleAccessToken , String coachEmail)
            throws IOException {
        try {
            Calendar service = getCalendarService(googleAccessToken);

            // Parse thời gian
            Instant instant = Instant.parse(request.getAppointmentDateTime());
            EventDateTime start =
                    new EventDateTime()
                            .setDateTime(new com.google.api.client.util.DateTime(
                                    DateTimeFormatter.ISO_INSTANT.format(instant)))
                            .setTimeZone(VIETNAM_ZONE.toString());
            EventDateTime end =
                    new EventDateTime()
                            .setDateTime(new com.google.api.client.util.DateTime(
                                    DateTimeFormatter.ISO_INSTANT.format(instant.plusSeconds(3600))))
                            .setTimeZone(VIETNAM_ZONE.toString());
            List<EventAttendee> attendees = Arrays.asList(
                    new EventAttendee()
                            .setEmail(userEmail)
                            .setResponseStatus("accepted"),  // User tự động accept
                    new EventAttendee()
                            .setEmail(coachEmail)
                            .setResponseStatus("accepted")  // Coach cần confirm
            );

            Event event = new Event()
                    .setSummary("Coaching Session with " + userEmail)
                    .setDescription("Coaching session created from Vegan Life")
                    .setStart(start)
                    .setEnd(end)
                    .setAttendees(attendees);

            // Thêm Google Meet
            ConferenceSolutionKey solutionKey = new ConferenceSolutionKey().setType("hangoutsMeet");
            CreateConferenceRequest createConferenceRequest =
                    new CreateConferenceRequest()
                            .setRequestId("identity-service-" + System.currentTimeMillis())
                            .setConferenceSolutionKey(solutionKey);
            ConferenceData conferenceData =
                    new ConferenceData().setCreateRequest(createConferenceRequest);
            event.setConferenceData(conferenceData);

            // Gửi request tạo event
            Event createdEvent = service.events()
                    .insert("primary", event)
                    .setConferenceDataVersion(1)
                    .setSendUpdates("all")
                    .execute();

            return createdEvent;
        } catch (GeneralSecurityException e) {
            throw new IOException("Error creating Google Calendar event", e);
        }
    }
    public void deleteEvent(String eventID, String googleAccessToken) throws GeneralSecurityException, IOException {
        Calendar service = getCalendarService(googleAccessToken);
        service.events().delete("primary", eventID).setSendUpdates("all").execute();
    }

}
