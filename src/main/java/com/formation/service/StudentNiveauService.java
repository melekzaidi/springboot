package com.formation.service;

import com.formation.entity.Niveau;
import com.formation.entity.Student;
import com.formation.entity.StudentNiveau;
import java.util.List;

public interface StudentNiveauService {
    StudentNiveau saveStudentNiveau(StudentNiveau studentNiveau);
    void deleteStudentNiveau(Long id);
    StudentNiveau getStudentNiveauById(Long id);
    List<StudentNiveau> getAllStudentNiveaux();
    boolean existsByStudentAndNiveau(Student student, Niveau niveau);
    List<StudentNiveau> getByStudent(Student student);
    List<StudentNiveau> getByNiveau(Niveau niveau);
}