package com.formation.controller.api;

import com.formation.dto.ApiResponseDTO;
import com.formation.dto.CourseNiveauDTO;
import com.formation.entity.CourseNiveau;
import com.formation.entity.Course;
import com.formation.entity.Niveau;
import com.formation.mapper.CourseNiveauMapper;
import com.formation.service.CourseNiveauService;
import com.formation.service.CourseService;
import com.formation.service.NiveauService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-niveaux")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CourseNiveauRestController {

    @Autowired
    private CourseNiveauService courseNiveauService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private NiveauService niveauService;

    @Autowired
    private CourseNiveauMapper courseNiveauMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<CourseNiveauDTO>>> getAllCourseNiveaux() {
        try {
            List<CourseNiveau> courseNiveaux = courseNiveauService.getAllCourseNiveaux();
            List<CourseNiveauDTO> courseNiveauDTOs = courseNiveauMapper.toDTOList(courseNiveaux);
            return ResponseEntity.ok(ApiResponseDTO.success(courseNiveauDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des cours-niveaux: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<CourseNiveauDTO>> getCourseNiveauById(@PathVariable Long id) {
        try {
            CourseNiveau courseNiveau = courseNiveauService.getCourseNiveauById(id);
            CourseNiveauDTO courseNiveauDTO = courseNiveauMapper.toDTO(courseNiveau);
            return ResponseEntity.ok(ApiResponseDTO.success(courseNiveauDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<CourseNiveauDTO>> createCourseNiveau(@Valid @RequestBody CourseNiveauDTO courseNiveauDTO) {
        try {
            Course course = courseService.getCourseById(courseNiveauDTO.getCourseId());
            Niveau niveau = niveauService.getNiveauById(courseNiveauDTO.getNiveauId());
            
            CourseNiveau courseNiveau = courseNiveauMapper.toEntity(courseNiveauDTO, course, niveau);
            CourseNiveau savedCourseNiveau = courseNiveauService.saveCourseNiveau(courseNiveau);
            CourseNiveauDTO savedCourseNiveauDTO = courseNiveauMapper.toDTO(savedCourseNiveau);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Cours-niveau créé avec succès", savedCourseNiveauDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la création du cours-niveau: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<CourseNiveauDTO>> updateCourseNiveau(
            @PathVariable Long id, 
            @Valid @RequestBody CourseNiveauDTO courseNiveauDTO) {
        try {
            CourseNiveau existingCourseNiveau = courseNiveauService.getCourseNiveauById(id);
            Course course = courseService.getCourseById(courseNiveauDTO.getCourseId());
            Niveau niveau = niveauService.getNiveauById(courseNiveauDTO.getNiveauId());
            
            courseNiveauMapper.updateEntityFromDTO(courseNiveauDTO, existingCourseNiveau, course, niveau);
            CourseNiveau updatedCourseNiveau = courseNiveauService.saveCourseNiveau(existingCourseNiveau);
            CourseNiveauDTO updatedCourseNiveauDTO = courseNiveauMapper.toDTO(updatedCourseNiveau);
            
            return ResponseEntity.ok(ApiResponseDTO.success("Cours-niveau modifié avec succès", updatedCourseNiveauDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la modification du cours-niveau: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<String>> deleteCourseNiveau(@PathVariable Long id) {
        try {
            courseNiveauService.deleteCourseNiveau(id);
            return ResponseEntity.ok(ApiResponseDTO.success("Cours-niveau supprimé avec succès", "OK"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la suppression du cours-niveau: " + e.getMessage()));
        }
    }

    @GetMapping("/by-course/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<CourseNiveauDTO>>> getCourseNiveauxByCourse(@PathVariable Long courseId) {
        try {
            List<CourseNiveau> courseNiveaux = courseNiveauService.getCourseNiveauxByCourseId(courseId);
            List<CourseNiveauDTO> courseNiveauDTOs = courseNiveauMapper.toDTOList(courseNiveaux);
            return ResponseEntity.ok(ApiResponseDTO.success(courseNiveauDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des cours-niveaux: " + e.getMessage()));
        }
    }

    @GetMapping("/by-niveau/{niveauId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<CourseNiveauDTO>>> getCourseNiveauxByNiveau(@PathVariable Long niveauId) {
        try {
            List<CourseNiveau> courseNiveaux = courseNiveauService.getCourseNiveauxByNiveauId(niveauId);
            List<CourseNiveauDTO> courseNiveauDTOs = courseNiveauMapper.toDTOList(courseNiveaux);
            return ResponseEntity.ok(ApiResponseDTO.success(courseNiveauDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des cours-niveaux: " + e.getMessage()));
        }
    }
}