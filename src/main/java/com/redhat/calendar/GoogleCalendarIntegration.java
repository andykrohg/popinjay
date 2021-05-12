package com.redhat.calendar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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
import com.google.api.services.calendar.model.ConferenceData;
import com.google.api.services.calendar.model.CreateConferenceRequest;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Organizer;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.redhat.domain.Mentor;
import com.redhat.domain.MentorAssignment;
import com.redhat.domain.Timeslot;
import com.redhat.domain.WingsRun;

public class GoogleCalendarIntegration {
    public static Map<String, List<Event>> schedules = Collections.synchronizedMap(new HashMap<String,  List<Event>>());
    public static LocalDate startDate = LocalDate.now().plusDays(8 - LocalDate.now().getDayOfWeek().getValue());

    private static final String APPLICATION_NAME = "Popinjay - Wings Scheduler";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String TIMEZONE = "America/New_York";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final List<String> SCENARIO_DOCUMENTS = Arrays.asList(
        "https://docs.google.com/document/d/1pmSj3YJsn0RPrCym5vSEFVTCvnoZjMkQ2kEBrCWzizc/edit?usp=sharing",
        "https://docs.google.com/document/d/1IgfW_2AxMh4xt8Bjquj1V0TkyDXEWLZW-zxACaqn5o4/edit?usp=sharing",
        "https://docs.google.com/document/d/1REIAGn_ZVlun2pQzQUkNF0NENg5K6YaKCHP0wVw4bXg/edit?usp=sharing",
        "https://docs.google.com/document/d/1x5fIPTop596bzJN0his1jLfsoiiMaaTVg-JP_B_mhEo/edit?usp=sharing",
        "https://docs.google.com/document/d/1zCRtwrRRDHbV0g3K454kPrKI87LEuT35Hu3n08IfHkQ/edit?usp=sharing",
        "https://docs.google.com/document/d/1nx7v0tFiHqR-f4EyrT5HUpvwRqL-pENvyjTCb6maXK0/edit?usp=sharing",
        "https://docs.google.com/document/d/1VqVO4Uoj-NOmkdDwx1npVk_EJiAcBj7D_EpULEqFGuo/edit?usp=sharing"
    );

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
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
                .setTimeMin(new DateTime(startDate.atTime(9,0).atZone(ZoneId.of(TIMEZONE)).toEpochSecond() * 1000))
                .setTimeMax(new DateTime(startDate.atTime(17,0).plusDays(4).atZone(ZoneId.of(TIMEZONE)).toEpochSecond() * 1000))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    } 

    public static void fetchSchedules() {
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

    public static boolean createEvent(WingsRun wingsRun, List<MentorAssignment> mentorAssignments) throws GeneralSecurityException, IOException {
        if (mentorAssignments == null || mentorAssignments.isEmpty() || mentorAssignments.stream().noneMatch(assignment -> assignment.getTimeslot() != null)) {
            System.err.println("No mentor assignments found. Refusing to create event for wingsRun " + wingsRun);
            return false;
        }

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // GET DATE INFO
        Timeslot timeslot = mentorAssignments.stream().filter(assignment -> {
            return assignment.getTimeslot() != null;
        }).findFirst().get().getTimeslot();

        LocalDate timeslotDate = GoogleCalendarIntegration.startDate
                .plusDays(timeslot.getDayOfWeek().getValue() - 1);
        LocalDateTime timeslotStart = timeslot.getStartTime().atDate(timeslotDate);
        LocalDateTime timeslotEnd = timeslot.getEndTime().atDate(timeslotDate);

        DateTime startDateTime = new DateTime(timeslotStart.atZone(ZoneId.of(TIMEZONE)).format(DateTimeFormatter.ofPattern(DATETIME_FORMAT)));
        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone(TIMEZONE);

        DateTime endDateTime = new DateTime(timeslotEnd.atZone(ZoneId.of(TIMEZONE)).format(DateTimeFormatter.ofPattern(DATETIME_FORMAT)));
        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone(TIMEZONE);
        
        Event event = new Event();

        event.setStart(start);
        event.setEnd(end);

        event.setSummary("🦜 Wings Run for " + wingsRun.getStudent() + ": " + wingsRun.getType());
        event.setDescription(
            "Your presence is kindly requested for this wings run panel.\n\n" +
            "Please refer to this document for the accompanying roles and scenario (if needed):\n" +
            SCENARIO_DOCUMENTS.get(new Random().nextInt(SCENARIO_DOCUMENTS.size()))
            );
        
        event.setConferenceData(
            new ConferenceData().setCreateRequest(
                new CreateConferenceRequest().setRequestId(wingsRun.toString())));

        List<EventAttendee> attendees = mentorAssignments.stream().filter(assignment -> assignment.getMentor() != null)
            .map(assignment -> {
                return new EventAttendee().setEmail(assignment.getMentor().getName() + "@redhat.com");
            }).collect(Collectors.toList());
        attendees.add(new EventAttendee().setEmail(wingsRun.getStudent() + "@redhat.com").setResponseStatus("accepted"));

        event.setOrganizer(new Organizer().setEmail(wingsRun.getStudent() + "@redhat.com"));

        event.setAttendees(attendees);

        service.events().insert(wingsRun.getStudent() + "@redhat.com", event).setConferenceDataVersion(1).execute();
        return true;
    }

    /**
     * Helper method to determine whether the provided
     * timeslot interferes with one or more events in the provided
     * schedule.
     * 
     * @param timeslot the timeslot
     * @param schedule      a {@link java.util.List} of
     *                      {@link com.google.api.services.calendar.model.Event}s
     *                      sourced from a Google Calendar
     * 
     * @return true if a conflict is found
     */
    public static boolean doesConflictExist(Timeslot timeslot, List<Event> schedule) {
        LocalDate timeslotDate = GoogleCalendarIntegration.startDate
                .plusDays(timeslot.getDayOfWeek().getValue() - 1);
        LocalDateTime timeslotStart = timeslot.getStartTime().atDate(timeslotDate);
        LocalDateTime timeslotEnd = timeslot.getEndTime().atDate(timeslotDate);

        return schedule.stream().anyMatch(event -> {
            LocalDateTime eventStart;
            LocalDateTime eventEnd;
            if (event.getStart().getDateTime() != null) {
                eventStart = LocalDateTime.parse(event.getStart().getDateTime().toString(),
                        DateTimeFormatter.ofPattern(DATETIME_FORMAT));
                eventEnd = LocalDateTime.parse(event.getEnd().getDateTime().toString(),
                        DateTimeFormatter.ofPattern(DATETIME_FORMAT));

            } else {
                eventStart = LocalDate.parse(event.getStart().getDate().toString()).atStartOfDay();
                eventEnd = LocalDate.parse(event.getEnd().getDate().toString()).plusDays(1).atStartOfDay();
            }

            return timeslotEnd.compareTo(eventStart) >= 0 && timeslotStart.compareTo(eventEnd) <= 0;
        });
    }
}
