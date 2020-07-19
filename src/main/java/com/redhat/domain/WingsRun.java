package com.redhat.domain;

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

    public WingsRun() {
    }

    public WingsRun(String student, Type type) {
        this.student = student;
        this.type = type;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public WingsRun id(Long id) {
        this.id = id;
        return this;
    }

    public WingsRun student(String student) {
        this.student = student;
        return this;
    }

    public WingsRun type(Type type) {
        this.type = type;
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
        return Objects.equals(student, wingsRun.student) && Objects.equals(type, wingsRun.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, type);
    }

    @Override
    public String toString() {
        return "{" + " student='" + getStudent() + "'" + ", type='" + getType() + "'" + "}";
    }

    public enum Type {
        MIDDLEWARE_OVERVIEW, DEEP_DIVE, FULL_RUN
    }

}