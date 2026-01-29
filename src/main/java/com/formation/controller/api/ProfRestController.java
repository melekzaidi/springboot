package com.formation.controller.api;

import com.formation.dto.ApiResponseDTO;
import com.formation.dto.ProfDTO;
import com.formation.entity.Prof;
import com.formation.mapper.ProfMapper;
import com.formation.service.ProfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profs")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProfRestController {

    @Autowired
    private ProfService profService;

    @Autowired
    private ProfMapper profMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<ProfDTO>>> getAllProfs() {
        try {
            List<Prof> profs = profService.getAllProfs();
            List<ProfDTO> profDTOs = profMapper.toDTOList(profs);
            return ResponseEntity.ok(ApiResponseDTO.success(profDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des professeurs: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<ProfDTO>> getProfById(@PathVariable Long id) {
        try {
            Prof prof = profService.getProfById(id);
            ProfDTO profDTO = profMapper.toDTO(prof);
            return ResponseEntity.ok(ApiResponseDTO.success(profDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<ProfDTO>> createProf(@Valid @RequestBody ProfDTO profDTO) {
        try {
            // Check uniqueness
            if (!profService.isMatriculeUnique(profDTO.getMatricule())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Ce matricule existe déjà"));
            }
            
            if (!profService.isEmailUnique(profDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Cet email existe déjà"));
            }

            Prof prof = profMapper.toEntity(profDTO);
            Prof savedProf = profService.saveProf(prof);
            ProfDTO savedProfDTO = profMapper.toDTO(savedProf);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Professeur créé avec succès", savedProfDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la création du professeur: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<ProfDTO>> updateProf(
            @PathVariable Long id, 
            @Valid @RequestBody ProfDTO profDTO) {
        try {
            Prof existingProf = profService.getProfById(id);
            
            // Check uniqueness for changed fields
            if (!existingProf.getMatricule().equals(profDTO.getMatricule()) &&
                !profService.isMatriculeUnique(profDTO.getMatricule())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Ce matricule existe déjà"));
            }
            
            if (!existingProf.getEmail().equals(profDTO.getEmail()) &&
                !profService.isEmailUnique(profDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Cet email existe déjà"));
            }

            Prof updatedProf = profService.updateProf(id, profMapper.toEntity(profDTO));
            ProfDTO updatedProfDTO = profMapper.toDTO(updatedProf);
            
            return ResponseEntity.ok(ApiResponseDTO.success("Professeur modifié avec succès", updatedProfDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la modification du professeur: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<String>> deleteProf(@PathVariable Long id) {
        try {
            profService.deleteProf(id);
            return ResponseEntity.ok(ApiResponseDTO.success("Professeur supprimé avec succès", "OK"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la suppression du professeur: " + e.getMessage()));
        }
    }
}