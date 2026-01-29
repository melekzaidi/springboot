package com.formation.mapper;

import com.formation.dto.CourseNiveauDTO;
import com.formation.entity.CourseNiveau;
import com.formation.entity.Course;
import com.formation.entity.Niveau;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseNiveauMapper {

    public CourseNiveauDTO toDTO(CourseNiveau courseNiveau) {
        if (courseNiveau == null) return null;
        
        CourseNiveauDTO dto = new CourseNiveauDTO(
            courseNiveau.getId(),
            courseNiveau.getCourse().getId(),
            courseNiveau.getNiveau().getId(),
            courseNiveau.getPrix(),
            courseNiveau.getDuree(),
            courseNiveau.getDescription(),
            courseNiveau.getDateCreation()
        );
        
        // Add nested information for display
        dto.setCourseName(courseNiveau.getCourse().getNom());
        dto.setCourseCode(courseNiveau.getCourse().getCode());
        dto.setNiveauName(courseNiveau.getNiveau().getNom());
        dto.setNiveauCode(courseNiveau.getNiveau().getCode());
        
        return dto;
    }

    public CourseNiveau toEntity(CourseNiveauDTO dto, Course course, Niveau niveau) {
        if (dto == null) return null;
        
        CourseNiveau courseNiveau = new CourseNiveau();
        courseNiveau.setId(dto.getId());
        courseNiveau.setCourse(course);
        courseNiveau.setNiveau(niveau);
        courseNiveau.setPrix(dto.getPrix());
        courseNiveau.setDuree(dto.getDuree());
        courseNiveau.setDescription(dto.getDescription());
        courseNiveau.setDateCreation(dto.getDateCreation());
        
        return courseNiveau;
    }

    public List<CourseNiveauDTO> toDTOList(List<CourseNiveau> courseNiveaux) {
        return courseNiveaux.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromDTO(CourseNiveauDTO dto, CourseNiveau courseNiveau, Course course, Niveau niveau) {
        if (dto == null || courseNiveau == null) return;
        
        courseNiveau.setCourse(course);
        courseNiveau.setNiveau(niveau);
        courseNiveau.setPrix(dto.getPrix());
        courseNiveau.setDuree(dto.getDuree());
        courseNiveau.setDescription(dto.getDescription());
    }
}