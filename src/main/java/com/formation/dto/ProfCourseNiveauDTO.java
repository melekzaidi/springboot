package com.formation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ProfCourseNiveauDTO {
    private Long id;
    
    @NotNull(message = "Le professeur est obligatoire")
    private Long profId;
    
    @NotNull(message = "Le cours-niveau est obligatoire")
    private Long courseNiveauId;
    
    @NotNull(message = "La date d'affectation est obligatoire")
    private LocalDate dateAffectation;
    
    private LocalDate dateCreation;
    
    // Nested objects for display
    private String profNom;
    private String profPrenom;
    private String profMatricule;
    private String courseName;
    private String niveauName;

    // Constructors
    public ProfCourseNiveauDTO() {}

    public ProfCourseNiveauDTO(Long id, Long profId, Long courseNiveauId, LocalDate dateAffectation, LocalDate dateCreation) {
        this.id = id;
        this.profId = profId;
        this.courseNiveauId = courseNiveauId;
        this.dateAffectation = dateAffectation;
        this.dateCreation = dateCreation;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getProfId() { return profId; }
    public void setProfId(Long profId) { this.profId = profId; }
    
    public Long getCourseNiveauId() { return courseNiveauId; }
    public void setCourseNiveauId(Long courseNiveauId) { this.courseNiveauId = courseNiveauId; }
    
    public LocalDate getDateAffectation() { return dateAffectation; }
    public void setDateAffectation(LocalDate dateAffectation) { this.dateAffectation = dateAffectation; }
    
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    
    public String getProfNom() { return profNom; }
    public void setProfNom(String profNom) { this.profNom = profNom; }
    
    public String getProfPrenom() { return profPrenom; }
    public void setProfPrenom(String profPrenom) { this.profPrenom = profPrenom; }
    
    public String getProfMatricule() { return profMatricule; }
    public void setProfMatricule(String profMatricule) { this.profMatricule = profMatricule; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getNiveauName() { return niveauName; }
    public void setNiveauName(String niveauName) { this.niveauName = niveauName; }
    
    public String getProfNomComplet() {
        return profPrenom + " " + profNom;
    }
}