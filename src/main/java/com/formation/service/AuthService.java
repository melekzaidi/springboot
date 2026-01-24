package com.formation.service;

import com.formation.entity.Prof;
import com.formation.entity.Student;
import com.formation.repository.ProfRepository;
import com.formation.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProfRepository profRepository;

    public Student loginStudent(String email) {
        return studentRepository.findByEmail(email).orElse(null);
    }

    public Prof loginProf(String email) {
        return profRepository.findByEmail(email).orElse(null);
    }
}
