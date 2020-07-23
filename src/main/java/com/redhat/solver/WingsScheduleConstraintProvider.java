package com.redhat.solver;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.api.services.calendar.model.Event;
import com.google.common.base.Objects;
import com.redhat.calendar.GoogleCalendarIntegration;
import com.redhat.domain.Mentor;
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

                // hard constraints
                timeslotConflict(constraintFactory), multipleAssignmentsInTimeslotConflict(constraintFactory),
                selfAssignmentConflict(constraintFactory),

                // medium consntraints
                studentCalendarConflict(constraintFactory), mentorCalendarConflict(constraintFactory),

                // soft
                mentorAffinity(constraintFactory), mentorPairing(constraintFactory),
                maxAssignmentsPerWeek(constraintFactory)
                // mentorAssigned(constraintFactory),
                // minimumPanelConflict(constraintFactory),
        };
    }

    private Constraint studentCalendarConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class).filter(mentorAssignment -> {
            LocalDate timeslotDate = GoogleCalendarIntegration.startDate
                    .plusDays(mentorAssignment.getTimeslot().getDayOfWeek().getValue() - 1);
            LocalDateTime timeslotStart = mentorAssignment.getTimeslot().getStartTime().atDate(timeslotDate);
            LocalDateTime timeslotEnd = mentorAssignment.getTimeslot().getEndTime().atDate(timeslotDate);

            return doesConflictExist(timeslotStart, timeslotEnd,
                    GoogleCalendarIntegration.schedules.get(mentorAssignment.getWingsRun().getStudent()));
        }).penalize("Student is unavailable for the requested timeslot", HardMediumSoftScore.ONE_MEDIUM);
    }

    private Constraint mentorCalendarConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class).filter(mentorAssignment -> {
            LocalDate timeslotDate = GoogleCalendarIntegration.startDate
                    .plusDays(mentorAssignment.getTimeslot().getDayOfWeek().getValue() - 1);
            LocalDateTime timeslotStart = mentorAssignment.getTimeslot().getStartTime().atDate(timeslotDate);
            LocalDateTime timeslotEnd = mentorAssignment.getTimeslot().getEndTime().atDate(timeslotDate);

            return mentorAssignment.getMentor() != null && doesConflictExist(timeslotStart, timeslotEnd,
                    GoogleCalendarIntegration.schedules.get(mentorAssignment.getMentor().getName()));
        }).penalize("Mentor is unavailable for the requested timeslot", HardMediumSoftScore.ONE_MEDIUM);
    }

    private Constraint mentorAssigned(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class).filter(mentorAssignment -> {
            return mentorAssignment.getMentor() != null;
        }).reward("Prefer mentors to be assigned", HardMediumSoftScore.ONE_SOFT);
    }

    private Constraint multipleAssignmentsInTimeslotConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class)
                .join(MentorAssignment.class, Joiners.equal(MentorAssignment::getMentor),
                        Joiners.lessThan(MentorAssignment::getId), 
                        Joiners.filtering((assignment1, assignment2) -> {
                            return assignment1.getMentor() != null 
                            && Objects.equal(assignment1.getTimeslot(), assignment2.getTimeslot());
                        }))
                .penalize("A mentor cannot have multiple assignments in the same timeslot.",
                        HardMediumSoftScore.ONE_HARD);
    }

    private Constraint timeslotConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class)
                .join(MentorAssignment.class, Joiners.equal(MentorAssignment::getWingsRun),
                        // Joiners.lessThan(MentorAssignment::getId),
                        Joiners.filtering((assignment1, assignment2) -> {
                            return !Objects.equal(assignment1.getTimeslot(), assignment2.getTimeslot());
                        }))
                .penalize("Mentor assignments for the same wings run must fall in the same timeslot",
                        HardMediumSoftScore.ONE_HARD);
    }

    private Constraint selfAssignmentConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class).filter(mentorAssignment -> {
            // if (mentorAssignment.getMentor() != null) {
            //     System.out.println("STUDENT: " + mentorAssignment.getWingsRun().getStudent() + "\n" +
            //                         "MENTOR: " + mentorAssignment.getMentor().getName());
            // }
            return mentorAssignment.getMentor() != null && mentorAssignment.getWingsRun().getStudent()
                    .equals(mentorAssignment.getMentor().getName());
        }).penalize("Students cannot be assigned to their own panel.", HardMediumSoftScore.ONE_HARD);
    }

    // MEDIUM CONSTRAINTS
    private Constraint mentorPairing(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class).filter(mentorAssignment -> {
            return mentorAssignment.getMentor() != null && Objects.equal(mentorAssignment.getMentor().getMentee(),
                    mentorAssignment.getWingsRun().getStudent());
        }).reward("Prefer to pair mentors with their mentees", HardMediumSoftScore.ONE_SOFT);
    }

    private Constraint maxAssignmentsPerWeek(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class)
                .groupBy(MentorAssignment::getMentor, ConstraintCollectors.count()).filter((mentor, numAssignments) -> {
                    return mentor != null && numAssignments > MAX_ASSIGNMENTS;
                }).penalize("Prefer mentors not to have more than " + MAX_ASSIGNMENTS + " assignments per week.",
                        HardMediumSoftScore.ONE_SOFT, (mentor, numAssignments) -> numAssignments - MAX_ASSIGNMENTS);
    }

    // SOFT CONSTRAINTS
    private Constraint mentorAffinity(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class).impact(
                "Prefer assignments to more actively engaged mentors", HardMediumSoftScore.ONE_SOFT,
                mentorAssignment -> {
                    return mentorAssignment.getMentor() == null ? 0 : mentorAssignment.getMentor().getAffinity();
                });
    }

    private Constraint minimumPanelConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class).filter(mentorAssignment -> {
            LocalDate timeslotDate = GoogleCalendarIntegration.startDate
                    .plusDays(mentorAssignment.getTimeslot().getDayOfWeek().getValue() - 1);
            LocalDateTime timeslotStart = mentorAssignment.getTimeslot().getStartTime().atDate(timeslotDate);
            LocalDateTime timeslotEnd = mentorAssignment.getTimeslot().getEndTime().atDate(timeslotDate);

            return Mentor.streamAll().map(mentor -> (Mentor) mentor).filter(mentor -> {
                return !doesConflictExist(timeslotStart, timeslotEnd,
                        GoogleCalendarIntegration.schedules.get(mentor.getName()));
            }).count() < 3;
        }).penalize("Three", HardMediumSoftScore.ONE_HARD);
    }

    /**
     * Helper method to determine whether a timeslot delineated by the provided
     * start and end time interferes with one or more events in the provided
     * schedule.
     * 
     * @param timeslotStart the start of the timeslot
     * @param timeslotEnd   the end of the timeslot
     * @param schedule      a {@link java.util.List} of
     *                      {@link com.google.api.services.calendar.model.Event}s
     *                      sourced from a Google Calendar
     * 
     * @return true if a conflict is found
     */
    private boolean doesConflictExist(LocalDateTime timeslotStart, LocalDateTime timeslotEnd, List<Event> schedule) {
        return schedule.stream().anyMatch(event -> {
            LocalDateTime eventStart;
            LocalDateTime eventEnd;
            if (event.getStart().getDateTime() != null) {
                eventStart = LocalDateTime.parse(event.getStart().getDateTime().toString(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
                eventEnd = LocalDateTime.parse(event.getEnd().getDateTime().toString(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

            } else {
                eventStart = LocalDate.parse(event.getStart().getDate().toString()).atStartOfDay();
                eventEnd = LocalDate.parse(event.getEnd().getDate().toString()).plusDays(1).atStartOfDay();
            }

            return timeslotEnd.compareTo(eventStart) >= 0 && timeslotStart.compareTo(eventEnd) <= 0;
        });
    }
}