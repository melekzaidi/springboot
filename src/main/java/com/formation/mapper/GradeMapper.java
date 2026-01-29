package com.formation.mapper;

import com.formation.dto.GradeDTO;
import com.formation.entity.Grade;
import com.formation.entity.Student;
import com.formation.entity.ProfCourseNiveau;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GradeMapper {

    public GradeDTO toDTO(Grade grade) {
        if (grade == null) return null;
        
        GradeDTO dto = new GradeDTO(
            grade.getId(),
            grade.getStudent().getId(),
            grade.getProfCourseNiveau().getId(),
            grade.getGradeType(),
            grade.getNote(),
            grade.getCoefficient(),
            grade.getDateEvaluation(),
            grade.getCommentaire()
        );
        
        dto.setDateCreation(grade.getDateCreation());
        dto.setDateModification(grade.getDateModification());
        
        // Add nested information for display
        Student student = grade.getStudent();
        ProfCourseNiveau profCourseNiveau = grade.getProfCourseNiveau();
        
        dto.setStudentNom(student.getNom());
        dto.setStudentPrenom(student.getPrenom());
        dto.setStudentMatricule(student.getMatricule());
        dto.setCourseName(profCourseNiveau.getCourseNiveau().getCourse().getNom());
        dto.setNiveauName(profCourseNiveau.getCourseNiveau().getNiveau().getNom());
        dto.setProfNom(profCourseNiveau.getProf().getNom());
        dto.setProfPrenom(profCourseNiveau.getProf().getPrenom());
        
        return dto;
    }

    public Grade toEntity(GradeDTO dto, Student student, ProfCourseNiveau profCourseNiveau) {
        if (dto == null) return null;
        
        Grade grade = new Grade();
        grade.setId(dto.getId());
        grade.setStudent(student);
        grade.setProfCourseNiveau(profCourseNiveau);
        grade.setGradeType(dto.getGradeType());
        grade.setNote(dto.getNote());
        grade.setCoefficient(dto.getCoefficient());
        grade.setDateEvaluation(dto.getDateEvaluation());
        grade.setCommentaire(dto.getCommentaire());
        
        return grade;
    }

    public List<GradeDTO> toDTOList(List<Grade> grades) {
        return grades.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromDTO(GradeDTO dto, Grade grade) {
        if (dto == null || grade == null) return;
        
        grade.setGradeType(dto.getGradeType());
        grade.setNote(dto.getNote());
        grade.setCoefficient(dto.getCoefficient());
        grade.setDateEvaluation(dto.getDateEvaluation());
        grade.setCommentaire(dto.getCommentaire());
    }
}