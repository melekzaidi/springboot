package com.formation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "professor_notes")
public class ProfessorNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prof_id", nullable = false)
    @NotNull(message = "Le professeur est obligatoire")
    private Prof professor;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull(message = "L'étudiant est obligatoire")
    private Student student;

    @NotBlank(message = "Le contenu de la note est obligatoire")
    @Size(max = 2000, message = "La note ne peut pas dépasser 2000 caractères")
    @Column(name = "note_content", columnDefinition = "TEXT")
    private String noteContent;

    @Column(name = "date_created")
    private LocalDateTime dateCreated = LocalDateTime.now();

    @Column(name = "date_updated")
    private LocalDateTime dateUpdated = LocalDateTime.now();

    // Optional: Link to specific course/session for context
    @ManyToOne
    @JoinColumn(name = "prof_course_niveau_id")
    private ProfCourseNiveau profCourseNiveau;

    @Column(name = "is_visible_to_student")
    private boolean isVisibleToStudent = true;

    // Constructors
    public ProfessorNote() {
    }

    public ProfessorNote(Prof professor, Student student, String noteContent) {
        this.professor = professor;
        this.student = student;
        this.noteContent = noteContent;
    }

    public ProfessorNote(Prof professor, Student student, String noteContent, ProfCourseNiveau profCourseNiveau) {
        this.professor = professor;
        this.student = student;
        this.noteContent = noteContent;
        this.profCourseNiveau = profCourseNiveau;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prof getProfessor() {
        return professor;
    }

    public void setProfessor(Prof professor) {
        this.professor = professor;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
        this.dateUpdated = LocalDateTime.now();
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

    public ProfCourseNiveau getProfCourseNiveau() {
        return profCourseNiveau;
    }

    public void setProfCourseNiveau(ProfCourseNiveau profCourseNiveau) {
        this.profCourseNiveau = profCourseNiveau;
    }

    public boolean isVisibleToStudent() {
        return isVisibleToStudent;
    }

    public void setVisibleToStudent(boolean visibleToStudent) {
        isVisibleToStudent = visibleToStudent;
    }

    @PreUpdate
    public void preUpdate() {
        this.dateUpdated = LocalDateTime.now();
    }
}