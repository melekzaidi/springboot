package com.formation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class SessionDTO {
    private Long id;
    
    @NotNull(message = "La date de début est obligatoire")
    private LocalDateTime startDateTime;
    
    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime endDateTime;
    
    @NotNull(message = "L'affectation professeur-cours-niveau est obligatoire")
    private Long profCourseNiveauId;
    
    private String description;
    private LocalDateTime dateCreation;
    
    // Nested objects for display
    private String profNom;
    private String courseName;
    private String niveauName;

    // Constructors
    public SessionDTO() {}

    public SessionDTO(Long id, LocalDateTime startDateTime, LocalDateTime endDateTime, Long profCourseNiveauId, String description, LocalDateTime dateCreation) {
        this.id = id;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.profCourseNiveauId = profCourseNiveauId;
        this.description = description;
        this.dateCreation = dateCreation;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }
    
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }
    
    public Long getProfCourseNiveauId() { return profCourseNiveauId; }
    public void setProfCourseNiveauId(Long profCourseNiveauId) { this.profCourseNiveauId = profCourseNiveauId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public String getProfNom() { return profNom; }
    public void setProfNom(String profNom) { this.profNom = profNom; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getNiveauName() { return niveauName; }
    public void setNiveauName(String niveauName) { this.niveauName = niveauName; }
}