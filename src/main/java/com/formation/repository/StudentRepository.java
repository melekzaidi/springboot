package com.formation.repository;

import com.formation.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find by matricule
    Optional<Student> findByMatricule(String matricule);

    // Find by email
    Optional<Student> findByEmail(String email);

    // Find by nom containing (search)
    List<Student> findByNomContainingIgnoreCase(String nom);

    // Find by prenom containing (search)
    List<Student> findByPrenomContainingIgnoreCase(String prenom);

    // Find all ordered by nom
    List<Student> findAllByOrderByNomAsc();

    // Check if matricule exists
    boolean existsByMatricule(String matricule);

    // Check if email exists
    boolean existsByEmail(String email);

    // Custom query for statistics
    @Query("SELECT COUNT(s) FROM Student s")
    Long countTotalStudents();
}