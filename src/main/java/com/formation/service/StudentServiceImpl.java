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

    @Autowired
    private com.formation.repository.AppUserRepository appUserRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public Student saveStudent(Student student) {
        Student savedStudent = studentRepository.save(student);
        
        // Sync AppUser
        com.formation.entity.AppUser appUser = new com.formation.entity.AppUser();
        appUser.setEmail(savedStudent.getEmail());
        appUser.setPassword(passwordEncoder.encode(savedStudent.getMatricule())); // Password = Matricule
        appUser.setRole("STUDENT");
        appUserRepository.save(appUser);
        
        return savedStudent;
    }

    @Override
    public Student updateStudent(Long id, Student studentDetails) {
        Student student = getStudentById(id);
        
        String oldEmail = student.getEmail();
        
        student.setMatricule(studentDetails.getMatricule());
        student.setNom(studentDetails.getNom());
        student.setPrenom(studentDetails.getPrenom());
        student.setEmail(studentDetails.getEmail());
        student.setDateInscription(studentDetails.getDateInscription());

        Student updatedStudent = studentRepository.save(student);

        // Update AppUser if email changed
        if (!oldEmail.equals(updatedStudent.getEmail())) {
            appUserRepository.findByEmail(oldEmail).ifPresent(appUser -> {
                appUser.setEmail(updatedStudent.getEmail());
                appUserRepository.save(appUser);
            });
        }
        
        return updatedStudent;
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        // Delete AppUser
        appUserRepository.findByEmail(student.getEmail()).ifPresent(appUserRepository::delete);
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

    @Override
    public Student getStudentByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'email: " + email));
    }
}