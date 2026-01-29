package com.formation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class StudentNiveauDTO {
    private Long id;
    
    @NotNull(message = "L'étudiant est obligatoire")
    private Long studentId;
    
    @NotNull(message = "Le niveau est obligatoire")
    private Long niveauId;
    
    @NotNull(message = "La date d'inscription est obligatoire")
    private LocalDate dateInscription;
    
    private LocalDate dateCreation;
    
    // Nested objects for display
    private String studentNom;
    private String studentPrenom;
    private String studentMatricule;
    private String niveauName;
    private String niveauCode;

    // Constructors
    public StudentNiveauDTO() {}

    public StudentNiveauDTO(Long id, Long studentId, Long niveauId, LocalDate dateInscription, LocalDate dateCreation) {
        this.id = id;
        this.studentId = studentId;
        this.niveauId = niveauId;
        this.dateInscription = dateInscription;
        this.dateCreation = dateCreation;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public Long getNiveauId() { return niveauId; }
    public void setNiveauId(Long niveauId) { this.niveauId = niveauId; }
    
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
    
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    
    public String getStudentNom() { return studentNom; }
    public void setStudentNom(String studentNom) { this.studentNom = studentNom; }
    
    public String getStudentPrenom() { return studentPrenom; }
    public void setStudentPrenom(String studentPrenom) { this.studentPrenom = studentPrenom; }
    
    public String getStudentMatricule() { return studentMatricule; }
    public void setStudentMatricule(String studentMatricule) { this.studentMatricule = studentMatricule; }
    
    public String getNiveauName() { return niveauName; }
    public void setNiveauName(String niveauName) { this.niveauName = niveauName; }
    
    public String getNiveauCode() { return niveauCode; }
    public void setNiveauCode(String niveauCode) { this.niveauCode = niveauCode; }
    
    public String getStudentNomComplet() {
        return studentPrenom + " " + studentNom;
    }
}