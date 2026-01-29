package com.formation.service;

import com.formation.entity.CourseNiveau;
import com.formation.entity.Niveau;
import com.formation.entity.Student;
import com.formation.entity.StudentNiveau;
import com.formation.repository.StudentNiveauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentNiveauServiceImpl implements StudentNiveauService {

    @Autowired
    private StudentNiveauRepository studentNiveauRepository;

    @Autowired
    private CourseNiveauService courseNiveauService;

    @Override
    public StudentNiveau saveStudentNiveau(StudentNiveau studentNiveau) {
        return studentNiveauRepository.save(studentNiveau);
    }

    @Override
    public void deleteStudentNiveau(Long id) {
        StudentNiveau studentNiveau = getStudentNiveauById(id);
        studentNiveauRepository.delete(studentNiveau);
    }

    @Override
    public StudentNiveau getStudentNiveauById(Long id) {
        return studentNiveauRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation Étudiant-Niveau non trouvée avec l'ID: " + id));
    }

    @Override
    public List<StudentNiveau> getAllStudentNiveaux() {
        return studentNiveauRepository.findAll();
    }

    @Override
    public boolean existsByStudentAndNiveau(Student student, Niveau niveau) {
        return studentNiveauRepository.existsByStudentAndNiveau(student, niveau);
    }

    @Override
    public List<StudentNiveau> getByStudent(Student student) {
        return studentNiveauRepository.findByStudent(student);
    }

    @Override
    public List<StudentNiveau> getByNiveau(Niveau niveau) {
        return studentNiveauRepository.findByNiveau(niveau);
    }

    @Override
    public List<StudentNiveau> getStudentNiveauxByCourseNiveauId(Long courseNiveauId) {
        CourseNiveau courseNiveau = courseNiveauService.getCourseNiveauById(courseNiveauId);
        return getByNiveau(courseNiveau.getNiveau());
    }
}