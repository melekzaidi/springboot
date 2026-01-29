package com.formation.dto;

public class CourseNiveauDistributionDTO {
    private String courseName;
    private String niveauName;
    private String courseNiveauLabel;
    private Long studentCount;
    private Double percentage;

    public CourseNiveauDistributionDTO() {}

    public CourseNiveauDistributionDTO(String courseName, String niveauName, 
                                     String courseNiveauLabel, Long studentCount, Double percentage) {
        this.courseName = courseName;
        this.niveauName = niveauName;
        this.courseNiveauLabel = courseNiveauLabel;
        this.studentCount = studentCount;
        this.percentage = percentage;
    }

    // Getters and Setters
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

    public String getCourseNiveauLabel() {
        return courseNiveauLabel;
    }

    public void setCourseNiveauLabel(String courseNiveauLabel) {
        this.courseNiveauLabel = courseNiveauLabel;
    }

    public Long getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Long studentCount) {
        this.studentCount = studentCount;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}