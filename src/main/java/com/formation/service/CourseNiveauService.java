package com.formation.service;

import com.formation.entity.Course;
import com.formation.entity.CourseNiveau;
import com.formation.entity.Niveau;
import java.util.List;

public interface CourseNiveauService {
    CourseNiveau saveCourseNiveau(CourseNiveau courseNiveau);
    CourseNiveau updateCourseNiveau(Long id, CourseNiveau courseNiveau);
    void deleteCourseNiveau(Long id);
    CourseNiveau getCourseNiveauById(Long id);
    List<CourseNiveau> getAllCourseNiveaux();
    boolean existsByCourseAndNiveau(Course course, Niveau niveau);
    List<CourseNiveau> getByCourse(Course course);
    List<CourseNiveau> getByNiveau(Niveau niveau);
}