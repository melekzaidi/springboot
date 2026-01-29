package com.formation.service;

import com.formation.entity.CourseNiveau;
import com.formation.entity.Prof;
import com.formation.entity.ProfCourseNiveau;
import com.formation.repository.ProfCourseNiveauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProfCourseNiveauServiceImpl implements ProfCourseNiveauService {

    @Autowired
    private ProfCourseNiveauRepository profCourseNiveauRepository;

    @Autowired
    private ProfService profService;

    @Autowired
    private CourseNiveauService courseNiveauService;

    @Override
    public ProfCourseNiveau saveProfCourseNiveau(ProfCourseNiveau profCourseNiveau) {
        return profCourseNiveauRepository.save(profCourseNiveau);
    }

    @Override
    public void deleteProfCourseNiveau(Long id) {
        ProfCourseNiveau profCourseNiveau = getProfCourseNiveauById(id);
        profCourseNiveauRepository.delete(profCourseNiveau);
    }

    @Override
    public ProfCourseNiveau getProfCourseNiveauById(Long id) {
        return profCourseNiveauRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation Professeur-Cours-Niveau non trouvée avec l'ID: " + id));
    }

    @Override
    public List<ProfCourseNiveau> getAllProfCourseNiveaux() {
        return profCourseNiveauRepository.findAll();
    }

    @Override
    public boolean existsByProfAndCourseNiveau(Prof prof, CourseNiveau courseNiveau) {
        return profCourseNiveauRepository.existsByProfAndCourseNiveau(prof, courseNiveau);
    }

    @Override
    public List<ProfCourseNiveau> getByProf(Prof prof) {
        return profCourseNiveauRepository.findByProf(prof);
    }

    @Override
    public List<ProfCourseNiveau> getByCourseNiveau(CourseNiveau courseNiveau) {
        return profCourseNiveauRepository.findByCourseNiveau(courseNiveau);
    }

    @Override
    public List<ProfCourseNiveau> getProfCourseNiveauxByProfId(Long profId) {
        Prof prof = profService.getProfById(profId);
        return getByProf(prof);
    }

    @Override
    public List<ProfCourseNiveau> getProfCourseNiveauxByCourseNiveauId(Long courseNiveauId) {
        CourseNiveau courseNiveau = courseNiveauService.getCourseNiveauById(courseNiveauId);
        return getByCourseNiveau(courseNiveau);
    }
}