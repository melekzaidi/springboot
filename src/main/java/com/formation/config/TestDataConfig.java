package com.formation.config;

import com.formation.entity.AppUser;
import com.formation.entity.Student;
import com.formation.entity.Prof;
import com.formation.entity.Course;
import com.formation.entity.Niveau;
import com.formation.repository.AppUserRepository;
import com.formation.repository.StudentRepository;
import com.formation.repository.ProfRepository;
import com.formation.repository.CourseRepository;
import com.formation.repository.NiveauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class TestDataConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initTestData(
            AppUserRepository appUserRepository,
            StudentRepository studentRepository,
            ProfRepository profRepository,
            CourseRepository courseRepository,
            NiveauRepository niveauRepository) {
        
        return args -> {
            // Create test users if they don't exist
            if (!appUserRepository.existsByEmail("admin@formation.com")) {
                AppUser admin = new AppUser("admin@formation.com", passwordEncoder.encode("admin123"), "ADMIN");
                appUserRepository.save(admin);
            }

            if (!appUserRepository.existsByEmail("prof@formation.com")) {
                AppUser profUser = new AppUser("prof@formation.com", passwordEncoder.encode("prof123"), "PROF");
                appUserRepository.save(profUser);
                
                // Create corresponding Prof entity
                if (!profRepository.existsByEmail("prof@formation.com")) {
                    Prof prof = new Prof("PROF001", "Dupont", "Jean", "prof@formation.com", "Informatique");
                    prof.setTelephone("0123456789");
                    prof.setDateEmbauche(LocalDate.now().minusYears(2));
                    profRepository.save(prof);
                }
            }

            if (!appUserRepository.existsByEmail("student@formation.com")) {
                AppUser studentUser = new AppUser("student@formation.com", passwordEncoder.encode("student123"), "STUDENT");
                appUserRepository.save(studentUser);
                
                // Create corresponding Student entity
                if (!studentRepository.existsByEmail("student@formation.com")) {
                    Student student = new Student("STU001", "Martin", "Marie", "student@formation.com", LocalDate.now().minusMonths(6));
                    studentRepository.save(student);
                }
            }

            // Create test courses
            if (courseRepository.count() == 0) {
                Course java = new Course("JAVA", "Programmation Java", "Cours de programmation orientée objet avec Java");
                Course react = new Course("REACT", "Développement React", "Développement d'applications web avec React");
                Course spring = new Course("SPRING", "Framework Spring", "Développement backend avec Spring Boot");
                
                courseRepository.save(java);
                courseRepository.save(react);
                courseRepository.save(spring);
            }

            // Create test levels
            if (niveauRepository.count() == 0) {
                Niveau debutant = new Niveau("DEB", "Débutant", "Niveau débutant");
                Niveau intermediaire = new Niveau("INT", "Intermédiaire", "Niveau intermédiaire");
                Niveau avance = new Niveau("ADV", "Avancé", "Niveau avancé");
                
                niveauRepository.save(debutant);
                niveauRepository.save(intermediaire);
                niveauRepository.save(avance);
            }
        };
    }
}