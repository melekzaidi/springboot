package com.formation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class NiveauDTO {
    private Long id;
    
    @NotBlank(message = "Le code du niveau est obligatoire")
    @Size(min = 2, max = 10, message = "Le code doit avoir entre 2 et 10 caractères")
    private String code;
    
    @NotBlank(message = "Le nom du niveau est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit avoir entre 2 et 50 caractères")
    private String nom;
    
    private String description;
    private LocalDate dateCreation;

    // Constructors
    public NiveauDTO() {}

    public NiveauDTO(Long id, String code, String nom, String description, LocalDate dateCreation) {
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