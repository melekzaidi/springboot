package com.formation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class CourseDTO {
    private Long id;
    
    @NotBlank(message = "Le code du cours est obligatoire")
    @Size(min = 3, max = 10, message = "Le code doit avoir entre 3 et 10 caractères")
    private String code;
    
    @NotBlank(message = "Le nom du cours est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit avoir entre 3 et 100 caractères")
    private String nom;
    
    private String description;
    private LocalDate dateCreation;

    // Constructors
    public CourseDTO() {}

    public CourseDTO(Long id, String code, String nom, String description, LocalDate dateCreation) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.description = description;
        this.dateCreation = dateCreation;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
}