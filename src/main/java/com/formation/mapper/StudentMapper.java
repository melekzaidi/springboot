package com.formation.mapper;

import com.formation.dto.StudentDTO;
import com.formation.entity.Student;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    public StudentDTO toDTO(Student student) {
        if (student == null) return null;
        
        return new StudentDTO(
            student.getId(),
            student.getMatricule(),
            student.getNom(),
            student.getPrenom(),
            student.getEmail(),
            student.getDateInscription(),
            student.getDateCreation()
        );
    }

    public Student toEntity(StudentDTO dto) {
        if (dto == null) return null;
        
        Student student = new Student();
        student.setId(dto.getId());
        student.setMatricule(dto.getMatricule());
        student.setNom(dto.getNom());
        student.setPrenom(dto.getPrenom());
        student.setEmail(dto.getEmail());
        student.setDateInscription(dto.getDateInscription());
        student.setDateCreation(dto.getDateCreation());
        
        return student;
    }

    public List<StudentDTO> toDTOList(List<Student> students) {
        return students.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromDTO(StudentDTO dto, Student student) {
        if (dto == null || student == null) return;
        
        student.setMatricule(dto.getMatricule());
        student.setNom(dto.getNom());
        student.setPrenom(dto.getPrenom());
        student.setEmail(dto.getEmail());
        student.setDateInscription(dto.getDateInscription());
    }
}