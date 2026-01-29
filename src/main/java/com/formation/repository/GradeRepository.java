package com.formation.repository;

import com.formation.entity.Grade;
import com.formation.entity.ProfCourseNiveau;
import com.formation.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    
    List<Grade> findByStudent(Student student);
    
    List<Grade> findByProfCourseNiveau(ProfCourseNiveau profCourseNiveau);
    
    List<Grade> findByStudentAndProfCourseNiveau(Student student, ProfCourseNiveau profCourseNiveau);
    
    Optional<Grade> findByStudentAndProfCourseNiveauAndGradeType(
        Student student, ProfCourseNiveau profCourseNiveau, Grade.GradeType gradeType);
    
    @Query("SELECT g FROM Grade g WHERE g.profCourseNiveau.prof.id = :profId")
    List<Grade> findByProfId(@Param("profId") Long profId);
    
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId")
    List<Grade> findByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT g FROM Grade g WHERE g.profCourseNiveau.id = :profCourseNiveauId")
    List<Grade> findByProfCourseNiveauId(@Param("profCourseNiveauId") Long profCourseNiveauId);
    
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.profCourseNiveau.id = :profCourseNiveauId")
    List<Grade> findByStudentIdAndProfCourseNiveauId(
        @Param("studentId") Long studentId, @Param("profCourseNiveauId") Long profCourseNiveauId);
    
    boolean existsByStudentAndProfCourseNiveauAndGradeType(
        Student student, ProfCourseNiveau profCourseNiveau, Grade.GradeType gradeType);
}