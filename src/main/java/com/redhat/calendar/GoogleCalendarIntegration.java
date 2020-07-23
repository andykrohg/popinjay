package com.redhat.calendar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.redhat.domain.Mentor;

public class GoogleCalendarIntegration {
    public static Map<String, List<Event>> schedules = Collections.synchronizedMap(new HashMap<String,  List<Event>>());
    public static LocalDate startDate = LocalDate.of(2020, 8, 17);//LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);

    private static final String APPLICATION_NAME = "Google Calendar Integration";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleCalendarIntegration.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).setHost("127.0.0.1").build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private static List<Event> getScheduleByUsername(String username) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        Events events = service.events().list(username + "@redhat.com")
                .setTimeMin(new DateTime(startDate.atTime(9,0).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000))
                .setTimeMax(new DateTime(startDate.atTime(17,0).plusDays(4).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    } 

    public static void fetchSchedules() {
        // startDate = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        System.out.println(startDate);

        Mentor.listAll().parallelStream().map(mentor -> (Mentor) mentor).forEach(mentor -> {
            try {
                System.out.println("Updating schedule for " + mentor.getName());
                List<Event> mentorSchedule = getScheduleByUsername(mentor.getName());
                schedules.put(mentor.getName(), mentorSchedule);
            } catch (IOException | GeneralSecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }
}
