package com.redhat.solver;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.common.base.Objects;
import com.redhat.calendar.GoogleCalendarIntegration;
import com.redhat.domain.MentorAssignment;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class WingsScheduleConstraintProvider implements ConstraintProvider {
    public static final int MAX_ASSIGNMENTS = 5;

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                // Hard constraints
                calendarConflict(constraintFactory),
                timeslotConflict(constraintFactory),
                multipleAssignmentsInTimeslotConflict(constraintFactory),
                mentorAffinity(constraintFactory),
                mentorPairing(constraintFactory),
                maxAssignmentsPerWeek(constraintFactory)
        };
    }

    // HARD CONSTRAINTS
    private Constraint calendarConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class).filter(mentorAssignment -> {
                LocalDate timeslotDate = GoogleCalendarIntegration.startDate.plusDays(mentorAssignment.getTimeslot().getDayOfWeek().getValue() - 1);
                LocalDateTime timeslotStart = mentorAssignment.getTimeslot().getStartTime().atDate(timeslotDate);
                LocalDateTime timeslotEnd = mentorAssignment.getTimeslot().getEndTime().atDate(timeslotDate);

                return doesConflictExist(timeslotStart, timeslotEnd, GoogleCalendarIntegration.schedules.get(mentorAssignment.getMentor().getName()))
                    || doesConflictExist(timeslotStart, timeslotEnd, GoogleCalendarIntegration.schedules.get(mentorAssignment.getWingsRun().getStudent()));
            })
        .penalize("Participant is unavailable for the requested timeslot", HardMediumSoftScore.ONE_HARD);
    }

    private Constraint multipleAssignmentsInTimeslotConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class)
            .join(MentorAssignment.class,
                Joiners.equal(MentorAssignment::getMentor),
                Joiners.equal(MentorAssignment::getTimeslot),
                Joiners.lessThan(MentorAssignment::getId))
            .penalize("A mentor cannot have multiple assignments in the same timeslot.", HardMediumSoftScore.ONE_HARD);
    }

    private Constraint timeslotConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class)
            .join(MentorAssignment.class,
                Joiners.equal(MentorAssignment::getWingsRun),
                Joiners.lessThan(MentorAssignment::getId),
                Joiners.filtering((assignment1, assignment2) -> {
                    return ! Objects.equal(assignment1.getTimeslot(), assignment2.getTimeslot());
                })
            )
            .penalize("Mentor assignments for the same wings run must fall in the same timeslot", HardMediumSoftScore.ONE_HARD);
    }

    // MEDIUM CONSTRAINTS
    private Constraint mentorPairing(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class)
            .filter(mentorAssignment -> {
                return Objects.equal(mentorAssignment.getMentor().getMentee(), mentorAssignment.getWingsRun().getStudent());
            })
            .reward("Prefer to pair mentors with their mentees", HardMediumSoftScore.ONE_MEDIUM);
    }

    private Constraint maxAssignmentsPerWeek(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class)
            .groupBy(MentorAssignment::getMentor, ConstraintCollectors.count())
            .filter((mentor, numAssignments) -> {
                return numAssignments > MAX_ASSIGNMENTS;
            })
            .penalize("Prefer mentors not to have more than " + MAX_ASSIGNMENTS + " assignments per week.", 
                HardMediumSoftScore.ONE_MEDIUM, (mentor, numAssignments) -> numAssignments - MAX_ASSIGNMENTS);
    }

    // SOFT CONSTRAINTS
    private Constraint mentorAffinity(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class).impact(
                "Prefer assignments to more actively engaged mentors", HardMediumSoftScore.ONE_SOFT,
                mentorAssignment -> mentorAssignment.getMentor().getAffinity());
    }

    private boolean doesConflictExist(LocalDateTime timeslotStart, LocalDateTime timeslotEnd, List<Event> schedule) {
        return schedule.stream().anyMatch(event -> {
            LocalDateTime eventStart;
            LocalDateTime eventEnd;
            if (event.getStart().getDateTime() != null) {
                eventStart = LocalDateTime.parse(event.getStart().getDateTime().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
                eventEnd = LocalDateTime.parse(event.getEnd().getDateTime().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

            } else {
                eventStart = LocalDate.parse(event.getStart().getDate().toString()).atStartOfDay();
                eventEnd = LocalDate.parse(event.getEnd().getDate().toString()).plusDays(1).atStartOfDay();
            }
            

            return timeslotEnd.compareTo(eventStart) >= 0 && timeslotStart.compareTo(eventEnd) <= 0;
        });
    }
}