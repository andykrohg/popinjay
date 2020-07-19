package com.redhat.solver;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.DayOfWeek;

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
                timeslotConflict(constraintFactory),
                multipleAssignmentsInTimeslotConflict(constraintFactory),
                calendarConflict(constraintFactory),
                mentorAffinity(constraintFactory),
                mentorPairing(constraintFactory),
                maxAssignmentsPerWeek(constraintFactory)
        };
    }

    // HARD CONSTRAINTS
    private Constraint calendarConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MentorAssignment.class).filter(mentorAssignment -> {
            try {
                return GoogleCalendarIntegration.getScheduleByUsername(mentorAssignment.getMentor().getName())
                        .parallelStream().anyMatch(event -> {
                            return mentorAssignment.getMentor().getName().equals("erchen");
                        });
            } catch (IOException | GeneralSecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return true;
            }
        }).penalize("Mentor is unavailable for the requested timeslot", HardMediumSoftScore.ONE_HARD);
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
                Joiners.lessThan(MentorAssignment::getId)
            )
            .filter((assignment1, assignment2) -> {
                return ! Objects.equal(assignment1.getTimeslot(), assignment2.getTimeslot());
            })
            .penalize("Mentor assignments for the same wings run must fall in the same time slot", HardMediumSoftScore.ONE_HARD);
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
}