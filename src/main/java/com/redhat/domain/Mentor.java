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
public class Mentor extends PanacheEntityBase {

    @PlanningId
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long id;

    private String name;

    // higher affinity = more assignments
    private int affinity;
    private String mentee;

    public Mentor() {
    }

    /**
     * 
     * @param name the mentor's username (e.g. Andy Krohg's is akrohg)
     * @param affinity a positive integer indicating this mentor's degree of involvement
     *             in wings. A higher affinity results in more panel assignments.
     * @param mentee the username of this mentor's mentee if applicable, otherwise null
     */
    public Mentor(String name, int affinity, String mentee) {
        this.name = name;
        this.affinity = affinity;
        this.mentee = mentee;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAffinity() {
        return this.affinity;
    }

    public void setAffinity(int affinity) {
        this.affinity = affinity;
    }

    public String getMentee() {
        return this.mentee;
    }

    public void setMentee(String mentee) {
        this.mentee = mentee;
    }

    public Mentor name(String name) {
        this.name = name;
        return this;
    }

    public Mentor affinity(int affinity) {
        this.affinity = affinity;
        return this;
    }

    public Mentor mentee(String mentee) {
        this.mentee = mentee;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Mentor)) {
            return false;
        }
        Mentor mentor = (Mentor) o;
        return Objects.equals(id, mentor.id) && Objects.equals(name, mentor.name) && affinity == mentor.affinity && Objects.equals(mentee, mentor.mentee);
    }
    
    @Override
    public String toString() {
        return "{" +
            " name='" + getName() + "'" +
            ", affinity='" + getAffinity() + "'" +
            ", mentee='" + getMentee() + "'" +
            "}";
    }

    public Mentor id(Long id) {
        this.id = id;
        return this;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}