package com.formation.controller.api;

import com.formation.dto.ApiResponseDTO;
import com.formation.dto.CourseDTO;
import com.formation.entity.Course;
import com.formation.mapper.CourseMapper;
import com.formation.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CourseRestController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseMapper courseMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<CourseDTO>>> getAllCourses() {
        try {
            List<Course> courses = courseService.getAllCourses();
            List<CourseDTO> courseDTOs = courseMapper.toDTOList(courses);
            return ResponseEntity.ok(ApiResponseDTO.success(courseDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des cours: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<CourseDTO>> getCourseById(@PathVariable Long id) {
        try {
            Course course = courseService.getCourseById(id);
            CourseDTO courseDTO = courseMapper.toDTO(course);
            return ResponseEntity.ok(ApiResponseDTO.success(courseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<CourseDTO>> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        try {
            // Check uniqueness
            if (!courseService.isCodeUnique(courseDTO.getCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Ce code existe déjà"));
            }

            Course course = courseMapper.toEntity(courseDTO);
            Course savedCourse = courseService.saveCourse(course);
            CourseDTO savedCourseDTO = courseMapper.toDTO(savedCourse);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Cours créé avec succès", savedCourseDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la création du cours: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<CourseDTO>> updateCourse(
            @PathVariable Long id, 
            @Valid @RequestBody CourseDTO courseDTO) {
        try {
            Course existingCourse = courseService.getCourseById(id);
            
            // Check uniqueness for changed code
            if (!existingCourse.getCode().equals(courseDTO.getCode()) &&
                !courseService.isCodeUnique(courseDTO.getCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Ce code existe déjà"));
            }

            Course updatedCourse = courseService.updateCourse(id, courseMapper.toEntity(courseDTO));
            CourseDTO updatedCourseDTO = courseMapper.toDTO(updatedCourse);
            
            return ResponseEntity.ok(ApiResponseDTO.success("Cours modifié avec succès", updatedCourseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la modification du cours: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<String>> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.ok(ApiResponseDTO.success("Cours supprimé avec succès", "OK"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la suppression du cours: " + e.getMessage()));
        }
    }
}