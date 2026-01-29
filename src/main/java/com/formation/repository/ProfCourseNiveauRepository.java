package com.formation.repository;

import com.formation.entity.CourseNiveau;
import com.formation.entity.Prof;
import com.formation.entity.ProfCourseNiveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfCourseNiveauRepository extends JpaRepository<ProfCourseNiveau, Long> {
    Optional<ProfCourseNiveau> findByProfAndCourseNiveau(Prof prof, CourseNiveau courseNiveau);
    List<ProfCourseNiveau> findByProf(Prof prof);
    List<ProfCourseNiveau> findByCourseNiveau(CourseNiveau courseNiveau);
    boolean existsByProfAndCourseNiveau(Prof prof, CourseNiveau courseNiveau);
    
    // Method for checking professor access to students
    @Query("SELECT pcn FROM ProfCourseNiveau pcn WHERE pcn.prof.id = :profId AND pcn.courseNiveau.niveau.id = :niveauId")
    List<ProfCourseNiveau> findByProfIdAndCourseNiveauNiveauId(@Param("profId") Long profId, @Param("niveauId") Long niveauId);
}