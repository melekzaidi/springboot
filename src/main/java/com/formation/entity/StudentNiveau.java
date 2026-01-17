package com.formation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "student_niveau", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "niveau_id"})
})
public class StudentNiveau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "niveau_id", nullable = false)
    private Niveau niveau;

    @NotNull(message = "La date d'inscription est obligatoire")
    @Column(name = "date_inscription")
    private LocalDate dateInscription = LocalDate.now();

    @Column(name = "date_creation")
    private LocalDate dateCreation = LocalDate.now();

    // Constructors
    public StudentNiveau() {
    }

    public StudentNiveau(Student student, Niveau niveau) {
        this.student = student;
        this.niveau = niveau;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Niveau getNiveau() {
        return niveau;
    }

    public void setNiveau(Niveau niveau) {
        this.niveau = niveau;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return student.getNom() + " - " + niveau.getNom();
    }
}