package com.formation.service;

import com.formation.entity.CourseNiveau;
import com.formation.entity.Prof;
import com.formation.entity.ProfCourseNiveau;
import java.util.List;

public interface ProfCourseNiveauService {
    ProfCourseNiveau saveProfCourseNiveau(ProfCourseNiveau profCourseNiveau);
    void deleteProfCourseNiveau(Long id);
    ProfCourseNiveau getProfCourseNiveauById(Long id);
    List<ProfCourseNiveau> getAllProfCourseNiveaux();
    boolean existsByProfAndCourseNiveau(Prof prof, CourseNiveau courseNiveau);
    List<ProfCourseNiveau> getByProf(Prof prof);
    List<ProfCourseNiveau> getByCourseNiveau(CourseNiveau courseNiveau);
    List<ProfCourseNiveau> getProfCourseNiveauxByProfId(Long profId);
    List<ProfCourseNiveau> getProfCourseNiveauxByCourseNiveauId(Long courseNiveauId);
}