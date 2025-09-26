//package com.devteria.identity_service.service;
//
//import com.devteria.identity_service.dto.CreateAppointmentRequest;
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.util.DateTime;
//import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.model.*;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.time.Instant;
//
//@Service
//public class GoogleCalendarService {
//    private static final String APPLICATION_NAME = "VeganLife";
//    private final Credential googleCredential;
//    private final NetHttpTransport httpTransport;
//    private final JsonFactory jsonFactory;
//    public GoogleCalendarService(Credential googleCredential,
//                                 NetHttpTransport httpTransport,
//                                 JsonFactory jsonFactory) {
//        this.googleCredential = googleCredential;
//        this.httpTransport = httpTransport;
//        this.jsonFactory = jsonFactory;
//    }
//    public String createGGMeetAppointment(CreateAppointmentRequest request) throws IOException {
//        Calendar service = new Calendar.Builder(
//                this.httpTransport,
//                this.jsonFactory,
//                this.googleCredential
//        ).setApplicationName(APPLICATION_NAME).build();
//        String startInstant = request.getAppointmentDateTime();
//        String endInstant = Instant.parse(startInstant).plusSeconds(60*60).toString();//1h
//        Event event = new Event()
//                .setSummary("Tư vấn và hỗ trợ với " + request.getCoachID())
//                .setDescription(request.getNotes())
//                .setStart(new EventDateTime()
//                        .setDateTime(new DateTime(startInstant.toString()))
//                        .setTimeZone("Asia/Ho_Chi_Minh"))
//                .setEnd(new EventDateTime()
//                        .setDateTime(new DateTime(endInstant.toString()))
//                        .setTimeZone("Asia/Ho_Chi_Minh"))
//                .setConferenceData(new ConferenceData()
//                        .setCreateRequest(new CreateConferenceRequest()
//                                .setRequestId("request-" + System.currentTimeMillis())
//                                .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet")
//                                )));
//        Event createdEvent = service.events()
//                .insert("primary", event)
//                .setConferenceDataVersion(1)
//                .execute();
//        System.out.println("Event created: " + createdEvent.getHtmlLink());
//        System.out.println("Google Meet Link: " + createdEvent.getHangoutLink());
//        return createdEvent.getHangoutLink();
//
//    }
//
//}
