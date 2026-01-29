package com.formation.controller.api;

import com.formation.dto.ApiResponseDTO;
import com.formation.dto.ProfessorNoteDTO;
import com.formation.service.ProfessorNoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/professor-notes")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProfessorNoteRestController {

    @Autowired
    private ProfessorNoteService professorNoteService;

    @PostMapping
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<ProfessorNoteDTO>> createNote(
            @Valid @RequestBody ProfessorNoteDTO noteDTO,
            Authentication authentication) {
        try {
            ProfessorNoteDTO createdNote = professorNoteService.createNote(noteDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Note créée avec succès", createdNote));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Erreur lors de la création de la note: " + e.getMessage()));
        }
    }

    @PutMapping("/{noteId}")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<ProfessorNoteDTO>> updateNote(
            @PathVariable Long noteId,
            @Valid @RequestBody ProfessorNoteDTO noteDTO,
            Authentication authentication) {
        try {
            String professorEmail = authentication.getName();
            ProfessorNoteDTO updatedNote = professorNoteService.updateNote(noteId, noteDTO, professorEmail);
            return ResponseEntity.ok(ApiResponseDTO.success("Note mise à jour avec succès", updatedNote));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Erreur lors de la mise à jour de la note: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{noteId}")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteNote(
            @PathVariable Long noteId,
            Authentication authentication) {
        try {
            String professorEmail = authentication.getName();
            professorNoteService.deleteNote(noteId, professorEmail);
            return ResponseEntity.ok(ApiResponseDTO.success("Note supprimée avec succès", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Erreur lors de la suppression de la note: " + e.getMessage()));
        }
    }

    @GetMapping("/{noteId}")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<ProfessorNoteDTO>> getNoteById(
            @PathVariable Long noteId,
            Authentication authentication) {
        try {
            String professorEmail = authentication.getName();
            ProfessorNoteDTO note = professorNoteService.getNoteById(noteId, professorEmail);
            return ResponseEntity.ok(ApiResponseDTO.success("Note récupérée avec succès", note));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error("Note non trouvée: " + e.getMessage()));
        }
    }

    @GetMapping("/my-notes")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<List<ProfessorNoteDTO>>> getMyNotes(Authentication authentication) {
        try {
            String professorEmail = authentication.getName();
            List<ProfessorNoteDTO> notes = professorNoteService.getNotesByProfessor(professorEmail);
            return ResponseEntity.ok(ApiResponseDTO.success("Notes récupérées avec succès", notes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des notes: " + e.getMessage()));
        }
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<List<ProfessorNoteDTO>>> getNotesByStudent(
            @PathVariable Long studentId,
            Authentication authentication) {
        try {
            String professorEmail = authentication.getName();
            List<ProfessorNoteDTO> notes = professorNoteService.getNotesByProfessorAndStudent(professorEmail, studentId);
            return ResponseEntity.ok(ApiResponseDTO.success("Notes récupérées avec succès", notes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des notes: " + e.getMessage()));
        }
    }

    @GetMapping("/course/{profCourseNiveauId}")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<List<ProfessorNoteDTO>>> getNotesByCourse(
            @PathVariable Long profCourseNiveauId,
            Authentication authentication) {
        try {
            String professorEmail = authentication.getName();
            List<ProfessorNoteDTO> notes = professorNoteService.getNotesByProfessorAndCourse(professorEmail, profCourseNiveauId);
            return ResponseEntity.ok(ApiResponseDTO.success("Notes récupérées avec succès", notes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des notes: " + e.getMessage()));
        }
    }

    // Endpoint for students to view their notes
    @GetMapping("/my-student-notes")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponseDTO<List<ProfessorNoteDTO>>> getMyStudentNotes(Authentication authentication) {
        try {
            String studentEmail = authentication.getName();
            List<ProfessorNoteDTO> notes = professorNoteService.getNotesForStudent(studentEmail);
            return ResponseEntity.ok(ApiResponseDTO.success("Notes récupérées avec succès", notes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des notes: " + e.getMessage()));
        }
    }

    @GetMapping("/my-student-notes/course/{profCourseNiveauId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponseDTO<List<ProfessorNoteDTO>>> getMyStudentNotesInCourse(
            @PathVariable Long profCourseNiveauId,
            Authentication authentication) {
        try {
            String studentEmail = authentication.getName();
            List<ProfessorNoteDTO> notes = professorNoteService.getNotesForStudentInCourse(studentEmail, profCourseNiveauId);
            return ResponseEntity.ok(ApiResponseDTO.success("Notes récupérées avec succès", notes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des notes: " + e.getMessage()));
        }
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<List<ProfessorNoteDTO>>> createBulkNotes(
            @Valid @RequestBody List<ProfessorNoteDTO> noteDTOs,
            Authentication authentication) {
        try {
            List<ProfessorNoteDTO> createdNotes = new ArrayList<>();
            for (ProfessorNoteDTO noteDTO : noteDTOs) {
                ProfessorNoteDTO createdNote = professorNoteService.createNote(noteDTO);
                createdNotes.add(createdNote);
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Notes créées avec succès", createdNotes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Erreur lors de la création des notes: " + e.getMessage()));
        }
    }

    @PutMapping("/bulk")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<List<ProfessorNoteDTO>>> updateBulkNotes(
            @Valid @RequestBody List<ProfessorNoteDTO> noteDTOs,
            Authentication authentication) {
        try {
            String professorEmail = authentication.getName();
            List<ProfessorNoteDTO> updatedNotes = new ArrayList<>();
            for (ProfessorNoteDTO noteDTO : noteDTOs) {
                if (noteDTO.getId() != null) {
                    ProfessorNoteDTO updatedNote = professorNoteService.updateNote(noteDTO.getId(), noteDTO, professorEmail);
                    updatedNotes.add(updatedNote);
                }
            }
            return ResponseEntity.ok(ApiResponseDTO.success("Notes mises à jour avec succès", updatedNotes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Erreur lors de la mise à jour des notes: " + e.getMessage()));
        }
    }

    @DeleteMapping("/bulk")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteBulkNotes(
            @RequestBody List<Long> noteIds,
            Authentication authentication) {
        try {
            String professorEmail = authentication.getName();
            for (Long noteId : noteIds) {
                professorNoteService.deleteNote(noteId, professorEmail);
            }
            return ResponseEntity.ok(ApiResponseDTO.success("Notes supprimées avec succès", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Erreur lors de la suppression des notes: " + e.getMessage()));
        }
    }
}