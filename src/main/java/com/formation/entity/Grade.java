package com.formation.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prof_course_niveau_id", nullable = false)
    private ProfCourseNiveau profCourseNiveau;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_type", nullable = false)
    private GradeType gradeType;

    @Column(name = "note", nullable = false)
    private Double note;

    @Column(name = "coefficient", nullable = false)
    private Double coefficient;

    @Column(name = "date_evaluation", nullable = false)
    private LocalDate dateEvaluation;

    @Column(name = "commentaire")
    private String commentaire;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructors
    public Grade() {
        this.dateCreation = LocalDateTime.now();
    }

    public Grade(Student student, ProfCourseNiveau profCourseNiveau, GradeType gradeType, 
                 Double note, Double coefficient, LocalDate dateEvaluation) {
        this();
        this.student = student;
        this.profCourseNiveau = profCourseNiveau;
        this.gradeType = gradeType;
        this.note = note;
        this.coefficient = coefficient;
        this.dateEvaluation = dateEvaluation;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public ProfCourseNiveau getProfCourseNiveau() { return profCourseNiveau; }
    public void setProfCourseNiveau(ProfCourseNiveau profCourseNiveau) { this.profCourseNiveau = profCourseNiveau; }

    public GradeType getGradeType() { return gradeType; }
    public void setGradeType(GradeType gradeType) { this.gradeType = gradeType; }

    public Double getNote() { return note; }
    public void setNote(Double note) { this.note = note; }

    public Double getCoefficient() { return coefficient; }
    public void setCoefficient(Double coefficient) { this.coefficient = coefficient; }

    public LocalDate getDateEvaluation() { return dateEvaluation; }
    public void setDateEvaluation(LocalDate dateEvaluation) { this.dateEvaluation = dateEvaluation; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    public enum GradeType {
        DS("Devoir Surveillé", 0.3),
        EXAMEN("Examen", 0.7);

        private final String displayName;
        private final Double defaultCoefficient;

        GradeType(String displayName, Double defaultCoefficient) {
            this.displayName = displayName;
            this.defaultCoefficient = defaultCoefficient;
        }

        public String getDisplayName() { return displayName; }
        public Double getDefaultCoefficient() { return defaultCoefficient; }
    }
}