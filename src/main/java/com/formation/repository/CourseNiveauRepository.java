package com.formation.repository;

import com.formation.entity.Course;
import com.formation.entity.CourseNiveau;
import com.formation.entity.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseNiveauRepository extends JpaRepository<CourseNiveau, Long> {
    Optional<CourseNiveau> findByCourseAndNiveau(Course course, Niveau niveau);
    List<CourseNiveau> findByCourse(Course course);
    List<CourseNiveau> findByNiveau(Niveau niveau);
    List<CourseNiveau> findByCourseId(Long courseId);
    List<CourseNiveau> findByNiveauId(Long niveauId);
    boolean existsByCourseAndNiveau(Course course, Niveau niveau);
}