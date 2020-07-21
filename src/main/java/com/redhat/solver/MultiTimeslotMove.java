package com.redhat.solver;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.redhat.domain.Mentor;
import com.redhat.domain.MentorAssignment;
import com.redhat.domain.Timeslot;
import com.redhat.domain.WingsSchedule;

import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class MultiTimeslotMove extends AbstractMove<WingsSchedule> {
    private HashMap<MentorAssignment, Mentor> mentorAssignments;
    private Timeslot toTimeslot;
    
    public MultiTimeslotMove(HashMap<MentorAssignment, Mentor> mentorAssignments, Timeslot toTimeslot) {
        this.mentorAssignments = mentorAssignments;
        this.toTimeslot = toTimeslot;
    }


    @Override
    public boolean isMoveDoable(ScoreDirector<WingsSchedule> scoreDirector) {
        //cannot move if the start and destination timeslots are the same
        return ! Objects.equals(mentorAssignments.keySet().stream().findFirst().get().getTimeslot(), toTimeslot);
    }

    @Override
    protected AbstractMove<WingsSchedule> createUndoMove(ScoreDirector<WingsSchedule> scoreDirector) {
        HashMap<MentorAssignment, Mentor> undoMap = new HashMap<MentorAssignment, Mentor>();
        mentorAssignments.entrySet().stream().forEach(entry -> {
            undoMap.put(entry.getKey(), entry.getKey().getMentor());
        });

        return new MultiTimeslotMove(undoMap, mentorAssignments.keySet().stream().findFirst().get().getTimeslot());
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<WingsSchedule> scoreDirector) {
        mentorAssignments.keySet().stream().forEach(mentorAssignment -> {
            scoreDirector.beforeVariableChanged(mentorAssignment, "timeslot");
            mentorAssignment.setTimeslot(toTimeslot);
            scoreDirector.afterVariableChanged(mentorAssignment, "timeslot");

            scoreDirector.beforeVariableChanged(mentorAssignment, "mentor");
            mentorAssignment.setMentor(mentorAssignments.get(mentorAssignment));
            scoreDirector.afterVariableChanged(mentorAssignment, "mentor");
        });
    }
    
    @Override
    public String getSimpleMoveTypeDescription() {
        return "MultiTimeslotMove(MentorAssignment.timeslot, MentorAssignment.mentor)";
    }


    @Override
    public MultiTimeslotMove rebase(ScoreDirector<WingsSchedule> destinationScoreDirector) {
        HashMap<MentorAssignment, Mentor> workingObjects = new HashMap<MentorAssignment, Mentor>();
        mentorAssignments.entrySet().stream().forEach(entry -> {
            MentorAssignment mentorAssignmentWO = destinationScoreDirector.lookUpWorkingObject(entry.getKey());
            Mentor mentorWO = null;

            if (entry.getValue() != null) {
                mentorWO = destinationScoreDirector.lookUpWorkingObject(entry.getValue());
            }

            workingObjects.put(mentorAssignmentWO, mentorWO);
        });

        return new MultiTimeslotMove(workingObjects, destinationScoreDirector.lookUpWorkingObject(toTimeslot));
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return mentorAssignments.keySet();
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        List<Object> planningValues = mentorAssignments.keySet().stream().map(mentorAssignment -> {
            return toTimeslot;
        }).collect(Collectors.toList());

        mentorAssignments.values().forEach(mentor -> {
            planningValues.add(mentor);
        });

        return planningValues;
    }

    public Timeslot getToTimeslot() {
        return this.toTimeslot;
    }

    public void setToTimeslot(Timeslot toTimeslot) {
        this.toTimeslot = toTimeslot;
    }

    public MultiTimeslotMove toTimeslot(Timeslot toTimeslot) {
        this.toTimeslot = toTimeslot;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MultiTimeslotMove)) {
            return false;
        }
        MultiTimeslotMove multiTimeslotMove = (MultiTimeslotMove) o;
        return Objects.equals(mentorAssignments, multiTimeslotMove.mentorAssignments) && Objects.equals(toTimeslot, multiTimeslotMove.toTimeslot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mentorAssignments, toTimeslot);
    }


    public Map<MentorAssignment,Mentor> getMentorAssignments() {
        return this.mentorAssignments;
    }

    public void setMentorAssignments(HashMap<MentorAssignment,Mentor> mentorAssignments) {
        this.mentorAssignments = mentorAssignments;
    }


    @Override
    public String toString() {
        return "{" +
            " mentorAssignments='" + mentorAssignments + "'" +
            ", toTimeslot='" + toTimeslot + "'" +
            "}";
    }


}