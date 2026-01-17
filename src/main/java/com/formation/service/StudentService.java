package com.formation.service;

import com.formation.entity.Student;
import java.util.List;
import java.util.Optional;

public interface StudentService {

    // CRUD operations
    Student saveStudent(Student student);

    Student updateStudent(Long id, Student student);

    void deleteStudent(Long id);

    Student getStudentById(Long id);

    List<Student> getAllStudents();

    // Business operations
    boolean isMatriculeUnique(String matricule);

    boolean isEmailUnique(String email);

    List<Student> searchStudents(String keyword);

    Long countStudents();

    // Find by matricule
    Optional<Student> findByMatricule(String matricule);
}