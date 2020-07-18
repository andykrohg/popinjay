package com.redhat.bootstrap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.transaction.Transactional;

import com.redhat.domain.Mentor;
import com.redhat.domain.MentorAssignment;
import com.redhat.domain.WingsRun;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class DemoDataGenerator {
    @Transactional
    public void generateDemoData(@Observes StartupEvent startupEvent) {
        // List<Timeslot> timeslotList = new ArrayList<>(10);
        // timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        // timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        // timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        // timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        // timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        // timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        // timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        // timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        // timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        // timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
        // if (demoData == DemoData.LARGE) {
        //     timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        //     timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        //     timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        //     timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        //     timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
        //     timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        //     timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        //     timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        //     timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        //     timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
        //     timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        //     timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        //     timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        //     timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        //     timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
        // }
        // Timeslot.persist(timeslotList);

        // List<Mentor> mentorList = new ArrayList<Mentor>();

        // List<Room> roomList = new ArrayList<>(3);
        // roomList.add(new Room("Room A"));
        // roomList.add(new Room("Room B"));
        // roomList.add(new Room("Room C"));

        // Room.persist(roomList);

        // List<Lesson> lessonList = new ArrayList<>();
        // lessonList.add(new Lesson("Math", "A. Turing", "9th grade"));
        // lessonList.add(new Lesson("Math", "A. Turing", "9th grade"));
        // lessonList.add(new Lesson("Physics", "M. Curie", "9th grade"));
        // lessonList.add(new Lesson("Chemistry", "M. Curie", "9th grade"));
        // lessonList.add(new Lesson("Biology", "C. Darwin", "9th grade"));
        // lessonList.add(new Lesson("History", "I. Jones", "9th grade"));
        // lessonList.add(new Lesson("English", "I. Jones", "9th grade"));
        // lessonList.add(new Lesson("English", "I. Jones", "9th grade"));
        // lessonList.add(new Lesson("Spanish", "P. Cruz", "9th grade"));
        // lessonList.add(new Lesson("Spanish", "P. Cruz", "9th grade"));


        // lessonList.add(new Lesson("Math", "A. Turing", "10th grade"));
        // lessonList.add(new Lesson("Math", "A. Turing", "10th grade"));
        // lessonList.add(new Lesson("Math", "A. Turing", "10th grade"));
        // lessonList.add(new Lesson("Physics", "M. Curie", "10th grade"));
        // lessonList.add(new Lesson("Chemistry", "M. Curie", "10th grade"));
        // lessonList.add(new Lesson("French", "M. Curie", "10th grade"));
        // lessonList.add(new Lesson("Geography", "C. Darwin", "10th grade"));
        // lessonList.add(new Lesson("History", "I. Jones", "10th grade"));
        // lessonList.add(new Lesson("English", "P. Cruz", "10th grade"));
        // lessonList.add(new Lesson("Spanish", "P. Cruz", "10th grade"));

        // Lesson lesson = lessonList.get(0);
        // lesson.setTimeslot(timeslotList.get(0));
        // lesson.setRoom(roomList.get(0));
        // Lesson.persist(lessonList);

        List<Mentor> mentorList = new ArrayList<Mentor>();
        mentorList.add(new Mentor("aromerot", 5, "afouladi"));
        mentorList.add(new Mentor("erchen", 6, null));
        mentorList.add(new Mentor("akrohg", 5, "rcabrera"));

        Mentor.persist(mentorList);

        List<WingsRun> wingsRunList = new ArrayList<WingsRun>();
        wingsRunList.add(new WingsRun("afouladi", WingsRun.Type.MIDDLEWARE_OVERVIEW, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())));

        WingsRun.persist(wingsRunList);

        List<MentorAssignment> mentorAssignmentList = new ArrayList<MentorAssignment>();
        wingsRunList.forEach(wingsRun -> {
            for (int i = 0; i < 3; i++) {
                mentorAssignmentList.add(new MentorAssignment(wingsRun, i));
            }
        });

        MentorAssignment.persist(mentorAssignmentList);
    }
}
