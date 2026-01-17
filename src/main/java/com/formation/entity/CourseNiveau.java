package com.formation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "course_niveau", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_id", "niveau_id"})
})
public class CourseNiveau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "niveau_id", nullable = false)
    private Niveau niveau;

    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix doit être positif")
    private Double prix;

    @NotNull(message = "La durée est obligatoire")
    @Min(value = 1, message = "La durée doit être d'au moins 1 heure")
    private Integer duree; // en heures

    private String description;

    @Column(name = "date_creation")
    private LocalDate dateCreation = LocalDate.now();

    @OneToMany(mappedBy = "courseNiveau", cascade = CascadeType.ALL)
    private Set<ProfCourseNiveau> profAssignments = new HashSet<>();

    // Constructors
    public CourseNiveau() {
    }

    public CourseNiveau(Course course, Niveau niveau, Double prix, Integer duree, String description) {
        this.course = course;
        this.niveau = niveau;
        this.prix = prix;
        this.duree = duree;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Niveau getNiveau() {
        return niveau;
    }

    public void setNiveau(Niveau niveau) {
        this.niveau = niveau;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Set<ProfCourseNiveau> getProfAssignments() {
        return profAssignments;
    }

    public void setProfAssignments(Set<ProfCourseNiveau> profAssignments) {
        this.profAssignments = profAssignments;
    }

    @Override
    public String toString() {
        return course.getNom() + " - " + niveau.getNom();
    }
}