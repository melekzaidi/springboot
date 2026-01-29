package com.formation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class ProfDTO {
    private Long id;
    
    @NotBlank(message = "Le matricule est obligatoire")
    @Size(min = 5, max = 20, message = "Le matricule doit avoir entre 5 et 20 caractères")
    private String matricule;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit avoir entre 2 et 50 caractères")
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit avoir entre 2 et 50 caractères")
    private String prenom;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;
    
    @NotBlank(message = "La spécialité est obligatoire")
    @Size(min = 2, max = 100, message = "La spécialité doit avoir entre 2 et 100 caractères")
    private String specialite;
    
    private String telephone;
    private LocalDate dateEmbauche;
    private LocalDate dateCreation;

    // Constructors
    public ProfDTO() {}

    public ProfDTO(Long id, String matricule, String nom, String prenom, String email, String specialite, String telephone, LocalDate dateEmbauche, LocalDate dateCreation) {
        this.id = id;
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.specialite = specialite;
        this.telephone = telephone;
        this.dateEmbauche = dateEmbauche;
        this.dateCreation = dateCreation;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }
    
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    
    public String getNomComplet() {
        return prenom + " " + nom;
    }
}