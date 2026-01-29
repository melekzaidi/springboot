package com.formation.dto;

public class GradeStatsDTO {
    private Long totalGrades;
    private Double averageGrade;
    private Long excellentGrades; // >= 16
    private Long goodGrades; // 14-15.99
    private Long averageGrades; // 12-13.99
    private Long poorGrades; // 10-11.99
    private Long failingGrades; // < 10
    private Double successRate; // >= 10
    private String topPerformingCourse;
    private String lowestPerformingCourse;

    public GradeStatsDTO() {}

    public GradeStatsDTO(Long totalGrades, Double averageGrade, Long excellentGrades, 
                        Long goodGrades, Long averageGrades, Long poorGrades, 
                        Long failingGrades, Double successRate, String topPerformingCourse, 
                        String lowestPerformingCourse) {
        this.totalGrades = totalGrades;
        this.averageGrade = averageGrade;
        this.excellentGrades = excellentGrades;
        this.goodGrades = goodGrades;
        this.averageGrades = averageGrades;
        this.poorGrades = poorGrades;
        this.failingGrades = failingGrades;
        this.successRate = successRate;
        this.topPerformingCourse = topPerformingCourse;
        this.lowestPerformingCourse = lowestPerformingCourse;
    }

    // Getters and Setters
    public Long getTotalGrades() {
        return totalGrades;
    }

    public void setTotalGrades(Long totalGrades) {
        this.totalGrades = totalGrades;
    }

    public Double getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(Double averageGrade) {
        this.averageGrade = averageGrade;
    }

    public Long getExcellentGrades() {
        return excellentGrades;
    }

    public void setExcellentGrades(Long excellentGrades) {
        this.excellentGrades = excellentGrades;
    }

    public Long getGoodGrades() {
        return goodGrades;
    }

    public void setGoodGrades(Long goodGrades) {
        this.goodGrades = goodGrades;
    }

    public Long getAverageGrades() {
        return averageGrades;
    }

    public void setAverageGrades(Long averageGrades) {
        this.averageGrades = averageGrades;
    }

    public Long getPoorGrades() {
        return poorGrades;
    }

    public void setPoorGrades(Long poorGrades) {
        this.poorGrades = poorGrades;
    }

    public Long getFailingGrades() {
        return failingGrades;
    }

    public void setFailingGrades(Long failingGrades) {
        this.failingGrades = failingGrades;
    }

    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }

    public String getTopPerformingCourse() {
        return topPerformingCourse;
    }

    public void setTopPerformingCourse(String topPerformingCourse) {
        this.topPerformingCourse = topPerformingCourse;
    }

    public String getLowestPerformingCourse() {
        return lowestPerformingCourse;
    }

    public void setLowestPerformingCourse(String lowestPerformingCourse) {
        this.lowestPerformingCourse = lowestPerformingCourse;
    }
}