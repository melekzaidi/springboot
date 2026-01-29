package com.formation.dto;

import java.time.LocalDateTime;

public class AttendanceDTO {
    private Long id;
    private Long sessionId;
    private Long studentId;
    private boolean isAbsent;
    private String comment;
    private LocalDateTime dateCreation;
    
    // Nested objects for display
    private String studentNom;
    private String studentPrenom;
    private String studentMatricule;

    // Constructors
    public AttendanceDTO() {}

    public AttendanceDTO(Long id, Long sessionId, Long studentId, boolean isAbsent, String comment, LocalDateTime dateCreation) {
        this.id = id;
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.isAbsent = isAbsent;
        this.comment = comment;
        this.dateCreation = dateCreation;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public boolean isAbsent() { return isAbsent; }
    public void setAbsent(boolean absent) { isAbsent = absent; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public String getStudentNom() { return studentNom; }
    public void setStudentNom(String studentNom) { this.studentNom = studentNom; }
    
    public String getStudentPrenom() { return studentPrenom; }
    public void setStudentPrenom(String studentPrenom) { this.studentPrenom = studentPrenom; }
    
    public String getStudentMatricule() { return studentMatricule; }
    public void setStudentMatricule(String studentMatricule) { this.studentMatricule = studentMatricule; }
    
    public String getStudentNomComplet() {
        return studentPrenom + " " + studentNom;
    }
}