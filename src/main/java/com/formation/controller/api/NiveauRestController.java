package com.formation.controller.api;

import com.formation.dto.ApiResponseDTO;
import com.formation.dto.NiveauDTO;
import com.formation.entity.Niveau;
import com.formation.mapper.NiveauMapper;
import com.formation.service.NiveauService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/niveaux")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class NiveauRestController {

    @Autowired
    private NiveauService niveauService;

    @Autowired
    private NiveauMapper niveauMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<NiveauDTO>>> getAllNiveaux() {
        try {
            List<Niveau> niveaux = niveauService.getAllNiveaux();
            List<NiveauDTO> niveauDTOs = niveauMapper.toDTOList(niveaux);
            return ResponseEntity.ok(ApiResponseDTO.success(niveauDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des niveaux: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<NiveauDTO>> getNiveauById(@PathVariable Long id) {
        try {
            Niveau niveau = niveauService.getNiveauById(id);
            NiveauDTO niveauDTO = niveauMapper.toDTO(niveau);
            return ResponseEntity.ok(ApiResponseDTO.success(niveauDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<NiveauDTO>> createNiveau(@Valid @RequestBody NiveauDTO niveauDTO) {
        try {
            if (!niveauService.isCodeUnique(niveauDTO.getCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Ce code existe déjà"));
            }

            Niveau niveau = niveauMapper.toEntity(niveauDTO);
            Niveau savedNiveau = niveauService.saveNiveau(niveau);
            NiveauDTO savedNiveauDTO = niveauMapper.toDTO(savedNiveau);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Niveau créé avec succès", savedNiveauDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la création du niveau: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<NiveauDTO>> updateNiveau(
            @PathVariable Long id, 
            @Valid @RequestBody NiveauDTO niveauDTO) {
        try {
            Niveau existingNiveau = niveauService.getNiveauById(id);
            
            if (!existingNiveau.getCode().equals(niveauDTO.getCode()) &&
                !niveauService.isCodeUnique(niveauDTO.getCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Ce code existe déjà"));
            }

            Niveau updatedNiveau = niveauService.updateNiveau(id, niveauMapper.toEntity(niveauDTO));
            NiveauDTO updatedNiveauDTO = niveauMapper.toDTO(updatedNiveau);
            
            return ResponseEntity.ok(ApiResponseDTO.success("Niveau modifié avec succès", updatedNiveauDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la modification du niveau: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<String>> deleteNiveau(@PathVariable Long id) {
        try {
            niveauService.deleteNiveau(id);
            return ResponseEntity.ok(ApiResponseDTO.success("Niveau supprimé avec succès", "OK"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la suppression du niveau: " + e.getMessage()));
        }
    }
}