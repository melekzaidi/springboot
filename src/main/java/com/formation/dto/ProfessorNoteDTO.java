package com.formation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class ProfessorNoteDTO {

    private Long id;

    @NotNull(message = "L'ID du professeur est obligatoire")
    private Long professorId;

    @NotNull(message = "L'ID de l'étudiant est obligatoire")
    private Long studentId;

    @NotBlank(message = "Le contenu de la note est obligatoire")
    @Size(max = 2000, message = "La note ne peut pas dépasser 2000 caractères")
    private String noteContent;

    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private Long profCourseNiveauId;
    private boolean isVisibleToStudent = true;

    // Additional fields for display
    private String professorName;
    private String studentName;
    private String courseName;
    private String niveauName;

    // Constructors
    public ProfessorNoteDTO() {
    }

    public ProfessorNoteDTO(Long professorId, Long studentId, String noteContent) {
        this.professorId = professorId;
        this.studentId = studentId;
        this.noteContent = noteContent;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Long getProfCourseNiveauId() {
        return profCourseNiveauId;
    }

    public void setProfCourseNiveauId(Long profCourseNiveauId) {
        this.profCourseNiveauId = profCourseNiveauId;
    }

    public boolean isVisibleToStudent() {
        return isVisibleToStudent;
    }

    public void setVisibleToStudent(boolean visibleToStudent) {
        this.isVisibleToStudent = visibleToStudent;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getNiveauName() {
        return niveauName;
    }

    public void setNiveauName(String niveauName) {
        this.niveauName = niveauName;
    }
}