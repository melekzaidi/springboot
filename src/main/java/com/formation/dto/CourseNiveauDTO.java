package com.formation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CourseNiveauDTO {
    private Long id;
    
    @NotNull(message = "Le cours est obligatoire")
    private Long courseId;
    
    @NotNull(message = "Le niveau est obligatoire")
    private Long niveauId;
    
    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix doit être positif")
    private Double prix;
    
    @NotNull(message = "La durée est obligatoire")
    @Min(value = 1, message = "La durée doit être d'au moins 1 heure")
    private Integer duree;
    
    private String description;
    private LocalDate dateCreation;
    
    // Nested objects for display
    private String courseName;
    private String courseCode;
    private String niveauName;
    private String niveauCode;

    // Constructors
    public CourseNiveauDTO() {}

    public CourseNiveauDTO(Long id, Long courseId, Long niveauId, Double prix, Integer duree, String description, LocalDate dateCreation) {
        this.id = id;
        this.courseId = courseId;
        this.niveauId = niveauId;
        this.prix = prix;
        this.duree = duree;
        this.description = description;
        this.dateCreation = dateCreation;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public Long getNiveauId() { return niveauId; }
    public void setNiveauId(Long niveauId) { this.niveauId = niveauId; }
    
    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }
    
    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    
    public String getNiveauName() { return niveauName; }
    public void setNiveauName(String niveauName) { this.niveauName = niveauName; }
    
    public String getNiveauCode() { return niveauCode; }
    public void setNiveauCode(String niveauCode) { this.niveauCode = niveauCode; }
}