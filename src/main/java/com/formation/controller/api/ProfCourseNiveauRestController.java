package com.formation.controller.api;

import com.formation.dto.ApiResponseDTO;
import com.formation.dto.ProfCourseNiveauDTO;
import com.formation.entity.ProfCourseNiveau;
import com.formation.entity.Prof;
import com.formation.entity.CourseNiveau;
import com.formation.mapper.ProfCourseNiveauMapper;
import com.formation.service.ProfCourseNiveauService;
import com.formation.service.ProfService;
import com.formation.service.CourseNiveauService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prof-course-niveaux")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProfCourseNiveauRestController {

    @Autowired
    private ProfCourseNiveauService profCourseNiveauService;

    @Autowired
    private ProfCourseNiveauMapper profCourseNiveauMapper;

    @Autowired
    private ProfService profService;

    @Autowired
    private CourseNiveauService courseNiveauService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<ProfCourseNiveauDTO>>> getAllProfCourseNiveaux() {
        try {
            List<ProfCourseNiveau> profCourseNiveaux = profCourseNiveauService.getAllProfCourseNiveaux();
            List<ProfCourseNiveauDTO> profCourseNiveauDTOs = profCourseNiveauMapper.toDTOList(profCourseNiveaux);
            return ResponseEntity.ok(ApiResponseDTO.success(profCourseNiveauDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des affectations: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<ProfCourseNiveauDTO>> getProfCourseNiveauById(@PathVariable Long id) {
        try {
            ProfCourseNiveau profCourseNiveau = profCourseNiveauService.getProfCourseNiveauById(id);
            ProfCourseNiveauDTO profCourseNiveauDTO = profCourseNiveauMapper.toDTO(profCourseNiveau);
            return ResponseEntity.ok(ApiResponseDTO.success(profCourseNiveauDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<ProfCourseNiveauDTO>> createProfCourseNiveau(@Valid @RequestBody ProfCourseNiveauDTO profCourseNiveauDTO) {
        try {
            // Get the Prof and CourseNiveau entities
            Prof prof = profService.getProfById(profCourseNiveauDTO.getProfId());
            CourseNiveau courseNiveau = courseNiveauService.getCourseNiveauById(profCourseNiveauDTO.getCourseNiveauId());
            
            ProfCourseNiveau profCourseNiveau = profCourseNiveauMapper.toEntity(profCourseNiveauDTO, prof, courseNiveau);
            profCourseNiveau = profCourseNiveauService.saveProfCourseNiveau(profCourseNiveau);
            ProfCourseNiveauDTO savedProfCourseNiveauDTO = profCourseNiveauMapper.toDTO(profCourseNiveau);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Affectation créée avec succès", savedProfCourseNiveauDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la création de l'affectation: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<ProfCourseNiveauDTO>> updateProfCourseNiveau(
            @PathVariable Long id, 
            @Valid @RequestBody ProfCourseNiveauDTO profCourseNiveauDTO) {
        try {
            ProfCourseNiveau existingProfCourseNiveau = profCourseNiveauService.getProfCourseNiveauById(id);
            
            // Get the Prof and CourseNiveau entities
            Prof prof = profService.getProfById(profCourseNiveauDTO.getProfId());
            CourseNiveau courseNiveau = courseNiveauService.getCourseNiveauById(profCourseNiveauDTO.getCourseNiveauId());
            
            profCourseNiveauMapper.updateEntityFromDTO(profCourseNiveauDTO, existingProfCourseNiveau, prof, courseNiveau);
            existingProfCourseNiveau = profCourseNiveauService.saveProfCourseNiveau(existingProfCourseNiveau);
            
            ProfCourseNiveauDTO updatedProfCourseNiveauDTO = profCourseNiveauMapper.toDTO(existingProfCourseNiveau);
            return ResponseEntity.ok(ApiResponseDTO.success("Affectation modifiée avec succès", updatedProfCourseNiveauDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la modification de l'affectation: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<String>> deleteProfCourseNiveau(@PathVariable Long id) {
        try {
            profCourseNiveauService.deleteProfCourseNiveau(id);
            return ResponseEntity.ok(ApiResponseDTO.success("Affectation supprimée avec succès", "OK"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la suppression de l'affectation: " + e.getMessage()));
        }
    }

    @GetMapping("/by-prof/{profId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<List<ProfCourseNiveauDTO>>> getProfCourseNiveauxByProf(@PathVariable Long profId) {
        try {
            List<ProfCourseNiveau> profCourseNiveaux = profCourseNiveauService.getProfCourseNiveauxByProfId(profId);
            List<ProfCourseNiveauDTO> profCourseNiveauDTOs = profCourseNiveauMapper.toDTOList(profCourseNiveaux);
            return ResponseEntity.ok(ApiResponseDTO.success(profCourseNiveauDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des affectations du professeur: " + e.getMessage()));
        }
    }

    @GetMapping("/by-course-niveau/{courseNiveauId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<ProfCourseNiveauDTO>>> getProfCourseNiveauxByCourseNiveau(@PathVariable Long courseNiveauId) {
        try {
            List<ProfCourseNiveau> profCourseNiveaux = profCourseNiveauService.getProfCourseNiveauxByCourseNiveauId(courseNiveauId);
            List<ProfCourseNiveauDTO> profCourseNiveauDTOs = profCourseNiveauMapper.toDTOList(profCourseNiveaux);
            return ResponseEntity.ok(ApiResponseDTO.success(profCourseNiveauDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des affectations pour le cours-niveau: " + e.getMessage()));
        }
    }
}