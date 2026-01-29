package com.formation.dto;

import com.formation.entity.Grade;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GradeDTO {
    private Long id;

    @NotNull(message = "L'étudiant est obligatoire")
    private Long studentId;

    @NotNull(message = "L'affectation professeur-cours-niveau est obligatoire")
    private Long profCourseNiveauId;

    @NotNull(message = "Le type de note est obligatoire")
    private Grade.GradeType gradeType;

    @NotNull(message = "La note est obligatoire")
    @DecimalMin(value = "0.0", message = "La note doit être supérieure ou égale à 0")
    @DecimalMax(value = "20.0", message = "La note doit être inférieure ou égale à 20")
    private Double note;

    @NotNull(message = "Le coefficient est obligatoire")
    @DecimalMin(value = "0.0", message = "Le coefficient doit être supérieur à 0")
    private Double coefficient;

    @NotNull(message = "La date d'évaluation est obligatoire")
    private LocalDate dateEvaluation;

    private String commentaire;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Nested information for display
    private String studentNom;
    private String studentPrenom;
    private String studentMatricule;
    private String courseName;
    private String niveauName;
    private String profNom;
    private String profPrenom;

    // Constructors
    public GradeDTO() {}

    public GradeDTO(Long id, Long studentId, Long profCourseNiveauId, Grade.GradeType gradeType,
                    Double note, Double coefficient, LocalDate dateEvaluation, String commentaire) {
        this.id = id;
        this.studentId = studentId;
        this.profCourseNiveauId = profCourseNiveauId;
        this.gradeType = gradeType;
        this.note = note;
        this.coefficient = coefficient;
        this.dateEvaluation = dateEvaluation;
        this.commentaire = commentaire;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getProfCourseNiveauId() { return profCourseNiveauId; }
    public void setProfCourseNiveauId(Long profCourseNiveauId) { this.profCourseNiveauId = profCourseNiveauId; }

    public Grade.GradeType getGradeType() { return gradeType; }
    public void setGradeType(Grade.GradeType gradeType) { this.gradeType = gradeType; }

    public Double getNote() { return note; }
    public void setNote(Double note) { this.note = note; }

    public Double getCoefficient() { return coefficient; }
    public void setCoefficient(Double coefficient) { this.coefficient = coefficient; }

    public LocalDate getDateEvaluation() { return dateEvaluation; }
    public void setDateEvaluation(LocalDate dateEvaluation) { this.dateEvaluation = dateEvaluation; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public String getStudentNom() { return studentNom; }
    public void setStudentNom(String studentNom) { this.studentNom = studentNom; }

    public String getStudentPrenom() { return studentPrenom; }
    public void setStudentPrenom(String studentPrenom) { this.studentPrenom = studentPrenom; }

    public String getStudentMatricule() { return studentMatricule; }
    public void setStudentMatricule(String studentMatricule) { this.studentMatricule = studentMatricule; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getNiveauName() { return niveauName; }
    public void setNiveauName(String niveauName) { this.niveauName = niveauName; }

    public String getProfNom() { return profNom; }
    public void setProfNom(String profNom) { this.profNom = profNom; }

    public String getProfPrenom() { return profPrenom; }
    public void setProfPrenom(String profPrenom) { this.profPrenom = profPrenom; }

    public String getStudentNomComplet() {
        return studentPrenom + " " + studentNom;
    }

    public String getProfNomComplet() {
        return profPrenom + " " + profNom;
    }

    public String getGradeTypeDisplayName() {
        return gradeType != null ? gradeType.getDisplayName() : "";
    }
}