package com.formation.dto;

import java.util.List;

public class CourseNiveauStatsDTO {
    private Long totalCourseNiveaux;
    private Long totalStudentEnrollments;
    private List<CourseNiveauDistributionDTO> distributions;
    private String mostPopularCourseNiveau;
    private String leastPopularCourseNiveau;

    public CourseNiveauStatsDTO() {}

    public CourseNiveauStatsDTO(Long totalCourseNiveaux, Long totalStudentEnrollments, 
                               List<CourseNiveauDistributionDTO> distributions,
                               String mostPopularCourseNiveau, String leastPopularCourseNiveau) {
        this.totalCourseNiveaux = totalCourseNiveaux;
        this.totalStudentEnrollments = totalStudentEnrollments;
        this.distributions = distributions;
        this.mostPopularCourseNiveau = mostPopularCourseNiveau;
        this.leastPopularCourseNiveau = leastPopularCourseNiveau;
    }

    // Getters and Setters
    public Long getTotalCourseNiveaux() {
        return totalCourseNiveaux;
    }

    public void setTotalCourseNiveaux(Long totalCourseNiveaux) {
        this.totalCourseNiveaux = totalCourseNiveaux;
    }

    public Long getTotalStudentEnrollments() {
        return totalStudentEnrollments;
    }

    public void setTotalStudentEnrollments(Long totalStudentEnrollments) {
        this.totalStudentEnrollments = totalStudentEnrollments;
    }

    public List<CourseNiveauDistributionDTO> getDistributions() {
        return distributions;
    }

    public void setDistributions(List<CourseNiveauDistributionDTO> distributions) {
        this.distributions = distributions;
    }

    public String getMostPopularCourseNiveau() {
        return mostPopularCourseNiveau;
    }

    public void setMostPopularCourseNiveau(String mostPopularCourseNiveau) {
        this.mostPopularCourseNiveau = mostPopularCourseNiveau;
    }

    public String getLeastPopularCourseNiveau() {
        return leastPopularCourseNiveau;
    }

    public void setLeastPopularCourseNiveau(String leastPopularCourseNiveau) {
        this.leastPopularCourseNiveau = leastPopularCourseNiveau;
    }
}