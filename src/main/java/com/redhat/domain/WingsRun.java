package com.redhat.domain;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.optaplanner.core.api.domain.lookup.PlanningId;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class WingsRun extends PanacheEntityBase {

    @PlanningId
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long id;

    private String student;
    private Type type;
    private Date startTime;
    private Date endTime;


    public WingsRun() {
    }

    public WingsRun(String student, Type type, Date startTime, Date endTime) {
        this.student = student;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStudent() {
        return this.student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public WingsRun student(String student) {
        this.student = student;
        return this;
    }

    public WingsRun type(Type type) {
        this.type = type;
        return this;
    }

    public WingsRun startTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public WingsRun endTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof WingsRun)) {
            return false;
        }
        WingsRun wingsRun = (WingsRun) o;
        return Objects.equals(student, wingsRun.student) && Objects.equals(type, wingsRun.type) && Objects.equals(startTime, wingsRun.startTime) && Objects.equals(endTime, wingsRun.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, type, startTime, endTime);
    }

    @Override
    public String toString() {
        return "{" +
            " student='" + getStudent() + "'" +
            ", type='" + getType() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }

    public enum Type {
        MIDDLEWARE_OVERVIEW, DEEP_DIVE, FULL_RUN
    }
    
}