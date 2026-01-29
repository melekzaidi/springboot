package com.formation.dto;

import java.util.List;

public class StudentGradesSummaryDTO {
    private Long studentId;
    private String studentNom;
    private String studentPrenom;
    private String studentMatricule;
    private Long profCourseNiveauId;
    private String courseName;
    private String niveauName;
    private String profNom;
    private String profPrenom;
    
    private Double noteDS;
    private Double noteExamen;
    private Double moyenneGenerale;
    
    private List<GradeDTO> grades;

    // Constructors
    public StudentGradesSummaryDTO() {}

    public StudentGradesSummaryDTO(Long studentId, String studentNom, String studentPrenom, 
                                   String studentMatricule, Long profCourseNiveauId, 
                                   String courseName, String niveauName, String profNom, String profPrenom) {
        this.studentId = studentId;
        this.studentNom = studentNom;
        this.studentPrenom = studentPrenom;
        this.studentMatricule = studentMatricule;
        this.profCourseNiveauId = profCourseNiveauId;
        this.courseName = courseName;
        this.niveauName = niveauName;
        this.profNom = profNom;
        this.profPrenom = profPrenom;
    }

    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentNom() { return studentNom; }
    public void setStudentNom(String studentNom) { this.studentNom = studentNom; }

    public String getStudentPrenom() { return studentPrenom; }
    public void setStudentPrenom(String studentPrenom) { this.studentPrenom = studentPrenom; }

    public String getStudentMatricule() { return studentMatricule; }
    public void setStudentMatricule(String studentMatricule) { this.studentMatricule = studentMatricule; }

    public Long getProfCourseNiveauId() { return profCourseNiveauId; }
    public void setProfCourseNiveauId(Long profCourseNiveauId) { this.profCourseNiveauId = profCourseNiveauId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getNiveauName() { return niveauName; }
    public void setNiveauName(String niveauName) { this.niveauName = niveauName; }

    public String getProfNom() { return profNom; }
    public void setProfNom(String profNom) { this.profNom = profNom; }

    public String getProfPrenom() { return profPrenom; }
    public void setProfPrenom(String profPrenom) { this.profPrenom = profPrenom; }

    public Double getNoteDS() { return noteDS; }
    public void setNoteDS(Double noteDS) { this.noteDS = noteDS; }

    public Double getNoteExamen() { return noteExamen; }
    public void setNoteExamen(Double noteExamen) { this.noteExamen = noteExamen; }

    public Double getMoyenneGenerale() { return moyenneGenerale; }
    public void setMoyenneGenerale(Double moyenneGenerale) { this.moyenneGenerale = moyenneGenerale; }

    public List<GradeDTO> getGrades() { return grades; }
    public void setGrades(List<GradeDTO> grades) { this.grades = grades; }

    public String getStudentNomComplet() {
        return studentPrenom + " " + studentNom;
    }

    public String getProfNomComplet() {
        return profPrenom + " " + profNom;
    }

    // Calculate moyenne based on DS and Examen notes
    public void calculateMoyenne() {
        if (noteDS != null && noteExamen != null) {
            this.moyenneGenerale = (0.3 * noteDS) + (0.7 * noteExamen);
        } else {
            this.moyenneGenerale = null;
        }
    }
}