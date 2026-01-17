package com.formation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "prof_course_niveau", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"prof_id", "course_niveau_id"})
})
public class ProfCourseNiveau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prof_id", nullable = false)
    private Prof prof;

    @ManyToOne
    @JoinColumn(name = "course_niveau_id", nullable = false)
    private CourseNiveau courseNiveau;

    @NotNull(message = "La date d'affectation est obligatoire")
    @Column(name = "date_affectation")
    private LocalDate dateAffectation = LocalDate.now();

    @Column(name = "date_creation")
    private LocalDate dateCreation = LocalDate.now();

    // Constructors
    public ProfCourseNiveau() {
    }

    public ProfCourseNiveau(Prof prof, CourseNiveau courseNiveau) {
        this.prof = prof;
        this.courseNiveau = courseNiveau;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prof getProf() {
        return prof;
    }

    public void setProf(Prof prof) {
        this.prof = prof;
    }

    public CourseNiveau getCourseNiveau() {
        return courseNiveau;
    }

    public void setCourseNiveau(CourseNiveau courseNiveau) {
        this.courseNiveau = courseNiveau;
    }

    public LocalDate getDateAffectation() {
        return dateAffectation;
    }

    public void setDateAffectation(LocalDate dateAffectation) {
        this.dateAffectation = dateAffectation;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return prof.getNomComplet() + " - " + courseNiveau.toString();
    }
}