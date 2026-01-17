package com.formation.service;

import com.formation.entity.Student;
import com.formation.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(Long id, Student studentDetails) {
        Student student = getStudentById(id);

        student.setMatricule(studentDetails.getMatricule());
        student.setNom(studentDetails.getNom());
        student.setPrenom(studentDetails.getPrenom());
        student.setEmail(studentDetails.getEmail());
        student.setDateInscription(studentDetails.getDateInscription());

        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'ID: " + id));
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAllByOrderByNomAsc();
    }

    @Override
    public boolean isMatriculeUnique(String matricule) {
        return !studentRepository.existsByMatricule(matricule);
    }

    @Override
    public boolean isEmailUnique(String email) {
        return !studentRepository.existsByEmail(email);
    }

    @Override
    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllStudents();
        }

        String searchTerm = "%" + keyword.toLowerCase() + "%";
        // You could implement a more complex search here
        return studentRepository.findByNomContainingIgnoreCase(keyword);
    }

    @Override
    public Long countStudents() {
        return studentRepository.countTotalStudents();
    }

    @Override
    public Optional<Student> findByMatricule(String matricule) {
        return studentRepository.findByMatricule(matricule);
    }
}