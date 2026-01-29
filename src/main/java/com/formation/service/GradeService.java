package com.formation.service;

import com.formation.dto.StudentGradesSummaryDTO;
import com.formation.entity.Grade;
import com.formation.entity.ProfCourseNiveau;
import com.formation.entity.Student;
import java.util.List;

public interface GradeService {
    Grade saveGrade(Grade grade);
    void deleteGrade(Long id);
    Grade getGradeById(Long id);
    List<Grade> getAllGrades();
    
    List<Grade> getGradesByStudent(Student student);
    List<Grade> getGradesByProfCourseNiveau(ProfCourseNiveau profCourseNiveau);
    List<Grade> getGradesByStudentAndProfCourseNiveau(Student student, ProfCourseNiveau profCourseNiveau);
    
    List<Grade> getGradesByProfId(Long profId);
    List<Grade> getGradesByStudentId(Long studentId);
    List<Grade> getGradesByProfCourseNiveauId(Long profCourseNiveauId);
    List<Grade> getGradesByStudentIdAndProfCourseNiveauId(Long studentId, Long profCourseNiveauId);
    
    boolean existsGradeForStudentAndProfCourseNiveauAndType(
        Student student, ProfCourseNiveau profCourseNiveau, Grade.GradeType gradeType);
    
    Grade findGradeByStudentAndProfCourseNiveauAndType(
        Student student, ProfCourseNiveau profCourseNiveau, Grade.GradeType gradeType);
    
    List<StudentGradesSummaryDTO> getStudentGradesSummaryByProfCourseNiveau(Long profCourseNiveauId);
    
    Double calculateMoyenneForStudent(Long studentId, Long profCourseNiveauId);
}