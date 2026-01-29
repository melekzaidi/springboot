package com.formation.controller.api;

import com.formation.dto.ApiResponseDTO;
import com.formation.dto.SessionDTO;
import com.formation.entity.Session;
import com.formation.entity.ProfCourseNiveau;
import com.formation.mapper.SessionMapper;
import com.formation.service.CalendarService;
import com.formation.service.ProfCourseNiveauService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SessionRestController {

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private ProfCourseNiveauService profCourseNiveauService;

    @Autowired
    private SessionMapper sessionMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<List<SessionDTO>>> getAllSessions(Authentication authentication) {
        try {
            String role = authentication.getAuthorities().stream()
                    .findFirst().get().getAuthority();
            
            List<Session> sessions;
            if ("ROLE_ADMIN".equals(role)) {
                sessions = calendarService.getAllSessions();
            } else {
                // For PROF, get only their sessions
                String email = authentication.getName();
                // We need to implement getSessionsForProfByEmail in CalendarService
                // For now, get all sessions and filter in service layer
                sessions = calendarService.getAllSessions();
            }
            
            List<SessionDTO> sessionDTOs = sessionMapper.toDTOList(sessions);
            return ResponseEntity.ok(ApiResponseDTO.success(sessionDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des séances: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<SessionDTO>> getSessionById(@PathVariable Long id) {
        try {
            Session session = calendarService.getSessionById(id);
            SessionDTO sessionDTO = sessionMapper.toDTO(session);
            return ResponseEntity.ok(ApiResponseDTO.success(sessionDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<SessionDTO>> createSession(@Valid @RequestBody SessionDTO sessionDTO) {
        try {
            ProfCourseNiveau profCourseNiveau = profCourseNiveauService.getProfCourseNiveauById(sessionDTO.getProfCourseNiveauId());
            Session session = sessionMapper.toEntity(sessionDTO, profCourseNiveau);
            
            calendarService.saveSession(session);
            SessionDTO savedSessionDTO = sessionMapper.toDTO(session);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Séance créée avec succès", savedSessionDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la création de la séance: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<SessionDTO>> updateSession(
            @PathVariable Long id, 
            @Valid @RequestBody SessionDTO sessionDTO) {
        try {
            Session existingSession = calendarService.getSessionById(id);
            ProfCourseNiveau profCourseNiveau = profCourseNiveauService.getProfCourseNiveauById(sessionDTO.getProfCourseNiveauId());
            
            sessionMapper.updateEntityFromDTO(sessionDTO, existingSession, profCourseNiveau);
            calendarService.saveSession(existingSession);
            
            SessionDTO updatedSessionDTO = sessionMapper.toDTO(existingSession);
            return ResponseEntity.ok(ApiResponseDTO.success("Séance modifiée avec succès", updatedSessionDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la modification de la séance: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<String>> deleteSession(@PathVariable Long id) {
        try {
            calendarService.deleteSession(id);
            return ResponseEntity.ok(ApiResponseDTO.success("Séance supprimée avec succès", "OK"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la suppression de la séance: " + e.getMessage()));
        }
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponseDTO<List<SessionDTO>>> getSessionsForStudent(@PathVariable Long studentId) {
        try {
            List<Session> sessions = calendarService.getSessionsForStudent(studentId);
            List<SessionDTO> sessionDTOs = sessionMapper.toDTOList(sessions);
            return ResponseEntity.ok(ApiResponseDTO.success(sessionDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des séances de l'étudiant: " + e.getMessage()));
        }
    }

    @GetMapping("/prof/{profId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<List<SessionDTO>>> getSessionsForProf(@PathVariable Long profId) {
        try {
            List<Session> sessions = calendarService.getSessionsForProf(profId);
            List<SessionDTO> sessionDTOs = sessionMapper.toDTOList(sessions);
            return ResponseEntity.ok(ApiResponseDTO.success(sessionDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des séances du professeur: " + e.getMessage()));
        }
    }
}