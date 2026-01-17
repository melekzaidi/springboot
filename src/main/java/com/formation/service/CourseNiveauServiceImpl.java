package com.formation.service;

import com.formation.entity.Course;
import com.formation.entity.CourseNiveau;
import com.formation.entity.Niveau;
import com.formation.repository.CourseNiveauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseNiveauServiceImpl implements CourseNiveauService {

    @Autowired
    private CourseNiveauRepository courseNiveauRepository;

    @Override
    public CourseNiveau saveCourseNiveau(CourseNiveau courseNiveau) {
        return courseNiveauRepository.save(courseNiveau);
    }

    @Override
    public CourseNiveau updateCourseNiveau(Long id, CourseNiveau courseNiveauDetails) {
        CourseNiveau courseNiveau = getCourseNiveauById(id);
        courseNiveau.setPrix(courseNiveauDetails.getPrix());
        courseNiveau.setDuree(courseNiveauDetails.getDuree());
        courseNiveau.setDescription(courseNiveauDetails.getDescription());
        return courseNiveauRepository.save(courseNiveau);
    }

    @Override
    public void deleteCourseNiveau(Long id) {
        CourseNiveau courseNiveau = getCourseNiveauById(id);
        courseNiveauRepository.delete(courseNiveau);
    }

    @Override
    public CourseNiveau getCourseNiveauById(Long id) {
        return courseNiveauRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Association Cours-Niveau non trouvée avec l'ID: " + id));
    }

    @Override
    public List<CourseNiveau> getAllCourseNiveaux() {
        return courseNiveauRepository.findAll();
    }

    @Override
    public boolean existsByCourseAndNiveau(Course course, Niveau niveau) {
        return courseNiveauRepository.existsByCourseAndNiveau(course, niveau);
    }

    @Override
    public List<CourseNiveau> getByCourse(Course course) {
        return courseNiveauRepository.findByCourse(course);
    }

    @Override
    public List<CourseNiveau> getByNiveau(Niveau niveau) {
        return courseNiveauRepository.findByNiveau(niveau);
    }
}