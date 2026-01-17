package com.formation.repository;

import com.formation.entity.Niveau;
import com.formation.entity.Student;
import com.formation.entity.StudentNiveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentNiveauRepository extends JpaRepository<StudentNiveau, Long> {
    Optional<StudentNiveau> findByStudentAndNiveau(Student student, Niveau niveau);
    List<StudentNiveau> findByStudent(Student student);
    List<StudentNiveau> findByNiveau(Niveau niveau);
    boolean existsByStudentAndNiveau(Student student, Niveau niveau);
}