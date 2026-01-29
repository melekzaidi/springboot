package com.formation.mapper;

import com.formation.dto.ProfCourseNiveauDTO;
import com.formation.entity.ProfCourseNiveau;
import com.formation.entity.Prof;
import com.formation.entity.CourseNiveau;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProfCourseNiveauMapper {

    public ProfCourseNiveauDTO toDTO(ProfCourseNiveau profCourseNiveau) {
        if (profCourseNiveau == null) return null;
        
        ProfCourseNiveauDTO dto = new ProfCourseNiveauDTO(
            profCourseNiveau.getId(),
            profCourseNiveau.getProf().getId(),
            profCourseNiveau.getCourseNiveau().getId(),
            profCourseNiveau.getDateAffectation(),
            profCourseNiveau.getDateCreation()
        );
        
        // Add nested information for display
        Prof prof = profCourseNiveau.getProf();
        CourseNiveau courseNiveau = profCourseNiveau.getCourseNiveau();
        
        dto.setProfNom(prof.getNom());
        dto.setProfPrenom(prof.getPrenom());
        dto.setProfMatricule(prof.getMatricule());
        dto.setCourseName(courseNiveau.getCourse().getNom());
        dto.setNiveauName(courseNiveau.getNiveau().getNom());
        
        return dto;
    }

    public ProfCourseNiveau toEntity(ProfCourseNiveauDTO dto, Prof prof, CourseNiveau courseNiveau) {
        if (dto == null) return null;
        
        ProfCourseNiveau profCourseNiveau = new ProfCourseNiveau();
        profCourseNiveau.setId(dto.getId());
        profCourseNiveau.setProf(prof);
        profCourseNiveau.setCourseNiveau(courseNiveau);
        profCourseNiveau.setDateAffectation(dto.getDateAffectation());
        profCourseNiveau.setDateCreation(dto.getDateCreation());
        
        return profCourseNiveau;
    }

    public List<ProfCourseNiveauDTO> toDTOList(List<ProfCourseNiveau> profCourseNiveaux) {
        return profCourseNiveaux.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromDTO(ProfCourseNiveauDTO dto, ProfCourseNiveau profCourseNiveau, Prof prof, CourseNiveau courseNiveau) {
        if (dto == null || profCourseNiveau == null) return;
        
        profCourseNiveau.setProf(prof);
        profCourseNiveau.setCourseNiveau(courseNiveau);
        profCourseNiveau.setDateAffectation(dto.getDateAffectation());
    }
}