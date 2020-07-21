package com.redhat.bootstrap;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.transaction.Transactional;

import com.redhat.domain.Mentor;
import com.redhat.domain.MentorAssignment;
import com.redhat.domain.Timeslot;
import com.redhat.domain.WingsRun;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class DemoDataGenerator {
    @Transactional
    public void generateDemoData(@Observes StartupEvent startupEvent) {
        List<Timeslot> timeslotList = new ArrayList<Timeslot>();
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(12, 00)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(15, 0)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)));
        // timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(19, 0), LocalTime.of(19, 30)));

        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(12, 00)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(13, 30), LocalTime.of(15, 0)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)));
        // timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(19, 0), LocalTime.of(19, 30)));


        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(10, 30), LocalTime.of(12, 00)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(13, 30), LocalTime.of(15, 0)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)));

        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(10, 30), LocalTime.of(12, 00)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(13, 30), LocalTime.of(15, 0)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)));

        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(10, 30), LocalTime.of(12, 00)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(13, 30), LocalTime.of(15, 0)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)));

        Timeslot.persist(timeslotList);

        List<Mentor> mentorList = new ArrayList<Mentor>();
        mentorList.add(new Mentor("aromerot", 5, "afouladi"));
        mentorList.add(new Mentor("erchen", 6, null));
        mentorList.add(new Mentor("akrohg", 5, "rcabrera"));
        mentorList.add(new Mentor("enham", 4, null));
        mentorList.add(new Mentor("adtaylor", 5, null));
        mentorList.add(new Mentor("lkinser", 5, null));
        mentorList.add(new Mentor("jrampras", 5, null));
        mentorList.add(new Mentor("micdavis", 5, null));
        mentorList.add(new Mentor("bcox", 4, null));
        mentorList.add(new Mentor("mkuriti", 4, null));


        

        mentorList.add(new Mentor("afouladi", 4, null));
        mentorList.add(new Mentor("rcabrera", 4, null));
        mentorList.add(new Mentor("scannata", 4, null));
        

        Mentor.persist(mentorList);

        List<WingsRun> wingsRunList = new ArrayList<WingsRun>();
        wingsRunList.add(new WingsRun("afouladi", WingsRun.Type.MIDDLEWARE_OVERVIEW));
        wingsRunList.add(new WingsRun("rcabrera", WingsRun.Type.FULL_RUN));
        wingsRunList.add(new WingsRun("scannata", WingsRun.Type.DEEP_DIVE));

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
