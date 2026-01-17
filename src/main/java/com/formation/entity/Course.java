package com.formation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le code du cours est obligatoire")
    @Size(min = 3, max = 10, message = "Le code doit avoir entre 3 et 10 caractères")
    @Column(unique = true, nullable = false)
    private String code;

    @NotBlank(message = "Le nom du cours est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit avoir entre 3 et 100 caractères")
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_creation")
    private LocalDate dateCreation = LocalDate.now();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<CourseNiveau> courseNiveaux = new HashSet<>();

    // Constructors
    public Course() {
    }

    public Course(String code, String nom, String description) {
        this.code = code;
        this.nom = nom;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public Set<CourseNiveau> getCourseNiveaux() {
        return courseNiveaux;
    }

    public void setCourseNiveaux(Set<CourseNiveau> courseNiveaux) {
        this.courseNiveaux = courseNiveaux;
    }

    @Override
    public String toString() {
        return nom + " (" + code + ")";
    }
}