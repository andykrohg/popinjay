package com.redhat.domain;

import java.util.List;
import java.util.Objects;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.solver.SolverStatus;

@PlanningSolution
public class WingsSchedule {
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "mentorRange")
    private List<Mentor> mentorList;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "timeslotRange")
    private List<Timeslot> timeslotList;

    @PlanningEntityCollectionProperty
    private List<MentorAssignment> mentorAssignments;

    @PlanningScore
    private HardMediumSoftScore score;

    private SolverStatus solverStatus;
    
    public WingsSchedule() {
    }

    public WingsSchedule(List<Mentor> mentorList, List<Timeslot> timeslotList, List<MentorAssignment> mentorAssignments) {
        this.mentorList = mentorList;
        this.timeslotList = timeslotList;
        this.mentorAssignments = mentorAssignments;
    }

    public List<Mentor> getMentorList() {
        return this.mentorList;
    }

    public void setMentorList(List<Mentor> mentorList) {
        this.mentorList = mentorList;
    }

    public List<MentorAssignment> getMentorAssignments() {
        return this.mentorAssignments;
    }

    public void setMentorAssignments(List<MentorAssignment> mentorAssignments) {
        this.mentorAssignments = mentorAssignments;
    }

    public HardMediumSoftScore getScore() {
        return this.score;
    }

    public void setScore(HardMediumSoftScore score) {
        this.score = score;
    }

    public WingsSchedule mentorList(List<Mentor> mentorList) {
        this.mentorList = mentorList;
        return this;
    }

    public WingsSchedule mentorAssignments(List<MentorAssignment> mentorAssignments) {
        this.mentorAssignments = mentorAssignments;
        return this;
    }

    public WingsSchedule score(HardMediumSoftScore score) {
        this.score = score;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof WingsSchedule)) {
            return false;
        }
        WingsSchedule wingsSchedule = (WingsSchedule) o;
        return Objects.equals(mentorList, wingsSchedule.mentorList) && Objects.equals(mentorAssignments, wingsSchedule.mentorAssignments) && Objects.equals(score, wingsSchedule.score);
    }

    // @Override
    // public int hashCode() {
    //     return Objects.hash(mentorList, mentorAssignments, score);
    // }

    @Override
    public String toString() {
        return "{" +
            " mentorList='" + getMentorList() + "'" +
            ", mentorAssignments='" + getMentorAssignments() + "'" +
            ", score='" + getScore() + "'" +
            "}";
    }

    public SolverStatus getSolverStatus() {
        return solverStatus;
    }

    public void setSolverStatus(SolverStatus solverStatus) {
        this.solverStatus = solverStatus;
    }

    public List<Timeslot> getTimeslotList() {
        return this.timeslotList;
    }

    public void setTimeslotList(List<Timeslot> timeslotList) {
        this.timeslotList = timeslotList;
    }
}