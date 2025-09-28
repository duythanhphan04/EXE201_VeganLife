package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.CreateAppointmentRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

@Service
public class GoogleCalendarService {

  private final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

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

  public String createGGMeetAppointment(
      CreateAppointmentRequest request, String userEmail, String googleAccessToken)
      throws IOException {
    try {
      Calendar service = getCalendarService(googleAccessToken);

      // Parse thời gian
      Instant instant = Instant.parse(request.getAppointmentDateTime());
      EventDateTime start =
          new EventDateTime()
              .setDateTime(
                  new com.google.api.client.util.DateTime(
                      DateTimeFormatter.ISO_INSTANT.format(instant)))
              .setTimeZone(VIETNAM_ZONE.toString());
      EventDateTime end =
          new EventDateTime()
              .setDateTime(
                  new com.google.api.client.util.DateTime(
                      DateTimeFormatter.ISO_INSTANT.format(instant.plusSeconds(60 * 60))))
              .setTimeZone(VIETNAM_ZONE.toString());

      Event event =
          new Event()
              .setSummary("Coaching Session with " + userEmail)
              .setDescription("Coaching session created from Identity Service")
              .setStart(start)
              .setEnd(end);

      // Thêm Google Meet
      ConferenceSolutionKey solutionKey = new ConferenceSolutionKey().setType("hangoutsMeet");
      CreateConferenceRequest createConferenceRequest =
          new CreateConferenceRequest()
              .setRequestId("identity-service-" + System.currentTimeMillis())
              .setConferenceSolutionKey(solutionKey);
      ConferenceData conferenceData =
          new ConferenceData().setCreateRequest(createConferenceRequest);
      event.setConferenceData(conferenceData);

      Event createdEvent =
          service.events().insert("primary", event).setConferenceDataVersion(1).execute();

      // Lấy link Google Meet
      if (createdEvent.getConferenceData() != null
          && createdEvent.getConferenceData().getEntryPoints() != null) {
        for (EntryPoint entryPoint : createdEvent.getConferenceData().getEntryPoints()) {
          if ("video".equals(entryPoint.getEntryPointType()) && entryPoint.getUri() != null) {
            return entryPoint.getUri();
          }
        }
      }

      return createdEvent.getHtmlLink();
    } catch (GeneralSecurityException e) {
      throw new IOException("Error creating Google Calendar event", e);
    }
  }
}
