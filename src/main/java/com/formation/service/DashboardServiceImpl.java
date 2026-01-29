package com.formation.service;

import com.formation.dto.CourseNiveauDistributionDTO;
import com.formation.dto.CourseNiveauStatsDTO;
import com.formation.repository.StudentNiveauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private StudentNiveauRepository studentNiveauRepository;

    @Override
    public CourseNiveauStatsDTO getCourseNiveauStats() {
        CourseNiveauStatsDTO stats = new CourseNiveauStatsDTO();
        
        try {
            System.out.println("DEBUG - Starting getCourseNiveauStats");
            
            // Get niveau distribution data
            List<Object[]> niveauDistribution = studentNiveauRepository.getNiveauStudentDistribution();
            System.out.println("DEBUG - Found " + niveauDistribution.size() + " niveau distributions");
            
            Long totalEnrollments = studentNiveauRepository.countTotalEnrolledStudents();
            System.out.println("DEBUG - Total enrollments: " + totalEnrollments);
            
            stats.setTotalStudentEnrollments(totalEnrollments != null ? totalEnrollments : 0L);
            stats.setTotalCourseNiveaux((long) niveauDistribution.size());
            
            // Convert to distribution DTOs
            List<CourseNiveauDistributionDTO> distributions = new ArrayList<>();
            String mostPopular = null;
            String leastPopular = null;
            Long maxCount = 0L;
            Long minCount = Long.MAX_VALUE;
            
            for (Object[] row : niveauDistribution) {
                String niveauName = (String) row[0];
                Long studentCount = (Long) row[1];
                
                System.out.println("DEBUG - Processing: " + niveauName + " with " + studentCount + " students");
                
                Double percentage = totalEnrollments > 0 ? (studentCount * 100.0) / totalEnrollments : 0.0;
                
                CourseNiveauDistributionDTO distribution = new CourseNiveauDistributionDTO();
                distribution.setNiveauName(niveauName);
                distribution.setCourseNiveauLabel(niveauName);
                distribution.setStudentCount(studentCount);
                distribution.setPercentage(percentage);
                
                distributions.add(distribution);
                
                // Track most and least popular
                if (studentCount > maxCount) {
                    maxCount = studentCount;
                    mostPopular = niveauName;
                }
                if (studentCount < minCount) {
                    minCount = studentCount;
                    leastPopular = niveauName;
                }
            }
            
            stats.setDistributions(distributions);
            stats.setMostPopularCourseNiveau(mostPopular);
            stats.setLeastPopularCourseNiveau(leastPopular);
            
            System.out.println("DEBUG - Final stats: " + distributions.size() + " distributions, most popular: " + mostPopular);
            
        } catch (Exception e) {
            System.err.println("ERROR in getCourseNiveauStats: " + e.getMessage());
            e.printStackTrace();
            // Fallback values in case of error
            stats.setTotalCourseNiveaux(0L);
            stats.setTotalStudentEnrollments(0L);
            stats.setDistributions(new ArrayList<>());
            stats.setMostPopularCourseNiveau("Aucun");
            stats.setLeastPopularCourseNiveau("Aucun");
        }
        
        return stats;
    }
}