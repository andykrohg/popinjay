package com.redhat.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@PlanningEntity
@Entity
public class MentorAssignment extends PanacheEntityBase {
    
    @PlanningId
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long id;

    @ManyToOne
    private WingsRun wingsRun;
    private int assignmentIndex; 

    @PlanningVariable(valueRangeProviderRefs = "mentorRange", nullable = true)
    @ManyToOne
    private Mentor mentor;

    @PlanningVariable(valueRangeProviderRefs = "timeslotRange")
    @ManyToOne
    private Timeslot timeslot;


    public MentorAssignment() {
    }

    public MentorAssignment(WingsRun wingsRun, int assignmentIndex) {
        this.wingsRun = wingsRun;
        this.assignmentIndex = assignmentIndex;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WingsRun getWingsRun() {
        return this.wingsRun;
    }

    public void setWingsRun(WingsRun wingsRun) {
        this.wingsRun = wingsRun;
    }

    public int getAssignmentIndex() {
        return this.assignmentIndex;
    }

    public void setAssignmentIndex(int assignmentIndex) {
        this.assignmentIndex = assignmentIndex;
    }

    public Mentor getMentor() {
        return this.mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public Timeslot getTimeslot() {
        return this.timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public MentorAssignment id(Long id) {
        this.id = id;
        return this;
    }

    public MentorAssignment wingsRun(WingsRun wingsRun) {
        this.wingsRun = wingsRun;
        return this;
    }

    public MentorAssignment assignmentIndex(int assignmentIndex) {
        this.assignmentIndex = assignmentIndex;
        return this;
    }

    public MentorAssignment mentor(Mentor mentor) {
        this.mentor = mentor;
        return this;
    }

    public MentorAssignment timeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MentorAssignment)) {
            return false;
        }
        MentorAssignment mentorAssignment = (MentorAssignment) o;
        return Objects.equals(id, mentorAssignment.id) && Objects.equals(wingsRun, mentorAssignment.wingsRun) && assignmentIndex == mentorAssignment.assignmentIndex && Objects.equals(mentor, mentorAssignment.mentor) && Objects.equals(timeslot, mentorAssignment.timeslot);
    }

    // @Override
    // public int hashCode() {
    //     return Objects.hash(id, wingsRun, assignmentIndex, mentor, timeslot);
    // }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", wingsRun='" + getWingsRun() + "'" +
            ", assignmentIndex='" + getAssignmentIndex() + "'" +
            ", mentor='" + getMentor() + "'" +
            ", timeslot='" + getTimeslot() + "'" +
            "}";
    }

}