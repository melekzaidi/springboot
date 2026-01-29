package com.formation.mapper;

import com.formation.dto.CourseDTO;
import com.formation.entity.Course;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseMapper {

    public CourseDTO toDTO(Course course) {
        if (course == null) return null;
        
        return new CourseDTO(
            course.getId(),
            course.getCode(),
            course.getNom(),
            course.getDescription(),
            course.getDateCreation()
        );
    }

    public Course toEntity(CourseDTO dto) {
        if (dto == null) return null;
        
        Course course = new Course();
        course.setId(dto.getId());
        course.setCode(dto.getCode());
        course.setNom(dto.getNom());
        course.setDescription(dto.getDescription());
        course.setDateCreation(dto.getDateCreation());
        
        return course;
    }

    public List<CourseDTO> toDTOList(List<Course> courses) {
        return courses.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromDTO(CourseDTO dto, Course course) {
        if (dto == null || course == null) return;
        
        course.setCode(dto.getCode());
        course.setNom(dto.getNom());
        course.setDescription(dto.getDescription());
    }
}