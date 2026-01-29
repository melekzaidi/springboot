package com.formation.controller.api;

import com.formation.dto.ApiResponseDTO;
import com.formation.dto.GradeDTO;
import com.formation.dto.ProfessorNoteDTO;
import com.formation.dto.StudentGradesSummaryDTO;
import com.formation.entity.Grade;
import com.formation.entity.ProfCourseNiveau;
import com.formation.entity.Student;
import com.formation.entity.Prof;
import com.formation.mapper.GradeMapper;
import com.formation.service.GradeService;
import com.formation.service.ProfCourseNiveauService;
import com.formation.service.ProfessorNoteService;
import com.formation.service.StudentService;
import com.formation.service.ProfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/integrated-notes")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class IntegratedNotesRestController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private ProfessorNoteService professorNoteService;

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ProfCourseNiveauService profCourseNiveauService;

    @Autowired
    private ProfService profService;

    /**
     * Get integrated data for students - both grades and remarks
     */
    @GetMapping("/student-data")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getStudentIntegratedData(Authentication authentication) {
        try {
            String email = authentication.getName();
            Student student = studentService.getStudentByEmail(email);
            
            // Get grades summary
            List<Grade> allGrades = gradeService.getGradesByStudentId(student.getId());
            Map<Long, List<Grade>> gradesByProfCourseNiveau = allGrades.stream()
                .collect(Collectors.groupingBy(grade -> grade.getProfCourseNiveau().getId()));
            
            List<StudentGradesSummaryDTO> gradesSummaries = new ArrayList<>();
            
            for (Map.Entry<Long, List<Grade>> entry : gradesByProfCourseNiveau.entrySet()) {
                Long profCourseNiveauId = entry.getKey();
                List<Grade> grades = entry.getValue();
                
                if (!grades.isEmpty()) {
                    Grade firstGrade = grades.get(0);
                    ProfCourseNiveau pcn = firstGrade.getProfCourseNiveau();
                    
                    StudentGradesSummaryDTO summary = new StudentGradesSummaryDTO(
                        student.getId(),
                        student.getNom(),
                        student.getPrenom(),
                        student.getMatricule(),
                        profCourseNiveauId,
                        pcn.getCourseNiveau().getCourse().getNom(),
                        pcn.getCourseNiveau().getNiveau().getNom(),
                        pcn.getProf().getNom(),
                        pcn.getProf().getPrenom()
                    );
                    
                    // Set DS and Examen notes
                    for (Grade grade : grades) {
                        if (grade.getGradeType() == Grade.GradeType.DS) {
                            summary.setNoteDS(grade.getNote());
                        } else if (grade.getGradeType() == Grade.GradeType.EXAMEN) {
                            summary.setNoteExamen(grade.getNote());
                        }
                    }
                    
                    // Calculate moyenne
                    summary.calculateMoyenne();
                    gradesSummaries.add(summary);
                }
            }
            
            // Get professor notes/remarks
            List<ProfessorNoteDTO> professorNotes = professorNoteService.getNotesForStudent(email);
            
            // Prepare response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("gradesSummary", gradesSummaries);
            responseData.put("professorNotes", professorNotes);
            responseData.put("studentInfo", Map.of(
                "id", student.getId(),
                "nom", student.getNom(),
                "prenom", student.getPrenom(),
                "matricule", student.getMatricule()
            ));
            
            return ResponseEntity.ok(ApiResponseDTO.success(responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des données: " + e.getMessage()));
        }
    }

    /**
     * Get integrated data for professors - grades they've given and notes they've written
     */
    @GetMapping("/professor-data")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getProfessorIntegratedData(Authentication authentication) {
        try {
            String email = authentication.getName();
            Prof prof = profService.getProfByEmail(email);
            
            // Get grades given by this professor
            List<Grade> myGrades = gradeService.getGradesByProfId(prof.getId());
            List<GradeDTO> gradesDTOs = gradeMapper.toDTOList(myGrades);
            
            // Get professor notes written by this professor
            List<ProfessorNoteDTO> myNotes = professorNoteService.getNotesByProfessor(email);
            
            // Get prof course niveaux for this professor
            List<ProfCourseNiveau> myProfCourseNiveaux = profCourseNiveauService.getProfCourseNiveauxByProfId(prof.getId());
            
            // Prepare response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("grades", gradesDTOs);
            responseData.put("professorNotes", myNotes);
            responseData.put("profCourseNiveaux", myProfCourseNiveaux);
            responseData.put("professorInfo", Map.of(
                "id", prof.getId(),
                "nom", prof.getNom(),
                "prenom", prof.getPrenom(),
                "email", prof.getEmail()
            ));
            
            return ResponseEntity.ok(ApiResponseDTO.success(responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des données: " + e.getMessage()));
        }
    }

    /**
     * Create a grade (for professors)
     */
    @PostMapping("/grades")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<GradeDTO>> createGrade(
            @Valid @RequestBody GradeDTO gradeDTO, 
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Prof prof = profService.getProfByEmail(email);
            
            // Verify that the prof-course-niveau belongs to the authenticated professor
            ProfCourseNiveau profCourseNiveau = profCourseNiveauService.getProfCourseNiveauById(gradeDTO.getProfCourseNiveauId());
            if (!profCourseNiveau.getProf().getId().equals(prof.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseDTO.error("Vous n'êtes pas autorisé à noter dans cette matière"));
            }
            
            // Get the Student entity
            Student student = studentService.getStudentById(gradeDTO.getStudentId());
            
            // Check if grade already exists for this type
            if (gradeService.existsGradeForStudentAndProfCourseNiveauAndType(student, profCourseNiveau, gradeDTO.getGradeType())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponseDTO.error("Une note de type " + gradeDTO.getGradeType().getDisplayName() + 
                                " existe déjà pour cet étudiant dans cette matière"));
            }
            
            // Set default coefficient if not provided
            if (gradeDTO.getCoefficient() == null) {
                gradeDTO.setCoefficient(gradeDTO.getGradeType().getDefaultCoefficient());
            }
            
            Grade grade = gradeMapper.toEntity(gradeDTO, student, profCourseNiveau);
            grade = gradeService.saveGrade(grade);
            GradeDTO savedGradeDTO = gradeMapper.toDTO(grade);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Note créée avec succès", savedGradeDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la création de la note: " + e.getMessage()));
        }
    }

    /**
     * Update a grade (for professors)
     */
    @PutMapping("/grades/{id}")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<GradeDTO>> updateGrade(
            @PathVariable Long id, 
            @Valid @RequestBody GradeDTO gradeDTO,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Prof prof = profService.getProfByEmail(email);
            
            Grade existingGrade = gradeService.getGradeById(id);
            
            // Verify that the grade belongs to this professor
            if (!existingGrade.getProfCourseNiveau().getProf().getId().equals(prof.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseDTO.error("Vous n'êtes pas autorisé à modifier cette note"));
            }
            
            gradeMapper.updateEntityFromDTO(gradeDTO, existingGrade);
            existingGrade = gradeService.saveGrade(existingGrade);
            
            GradeDTO updatedGradeDTO = gradeMapper.toDTO(existingGrade);
            return ResponseEntity.ok(ApiResponseDTO.success("Note modifiée avec succès", updatedGradeDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la modification de la note: " + e.getMessage()));
        }
    }

    /**
     * Delete a grade (for professors)
     */
    @DeleteMapping("/grades/{id}")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<String>> deleteGrade(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Prof prof = profService.getProfByEmail(email);
            
            Grade existingGrade = gradeService.getGradeById(id);
            
            // Verify that the grade belongs to this professor
            if (!existingGrade.getProfCourseNiveau().getProf().getId().equals(prof.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseDTO.error("Vous n'êtes pas autorisé à supprimer cette note"));
            }
            
            gradeService.deleteGrade(id);
            return ResponseEntity.ok(ApiResponseDTO.success("Note supprimée avec succès", "OK"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la suppression de la note: " + e.getMessage()));
        }
    }

    /**
     * Create a professor note/remark (for professors)
     */
    @PostMapping("/remarks")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<ProfessorNoteDTO>> createRemark(
            @Valid @RequestBody ProfessorNoteDTO noteDTO,
            Authentication authentication) {
        try {
            ProfessorNoteDTO createdNote = professorNoteService.createNote(noteDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Remarque créée avec succès", createdNote));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Erreur lors de la création de la remarque: " + e.getMessage()));
        }
    }

    /**
     * Update a professor note/remark (for professors)
     */
    @PutMapping("/remarks/{noteId}")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<ProfessorNoteDTO>> updateRemark(
            @PathVariable Long noteId,
            @Valid @RequestBody ProfessorNoteDTO noteDTO,
            Authentication authentication) {
        try {
            String professorEmail = authentication.getName();
            ProfessorNoteDTO updatedNote = professorNoteService.updateNote(noteId, noteDTO, professorEmail);
            return ResponseEntity.ok(ApiResponseDTO.success("Remarque mise à jour avec succès", updatedNote));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Erreur lors de la mise à jour de la remarque: " + e.getMessage()));
        }
    }

    /**
     * Delete a professor note/remark (for professors)
     */
    @DeleteMapping("/remarks/{noteId}")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteRemark(
            @PathVariable Long noteId,
            Authentication authentication) {
        try {
            String professorEmail = authentication.getName();
            professorNoteService.deleteNote(noteId, professorEmail);
            return ResponseEntity.ok(ApiResponseDTO.success("Remarque supprimée avec succès", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Erreur lors de la suppression de la remarque: " + e.getMessage()));
        }
    }

    /**
     * Get grade types
     */
    @GetMapping("/grade-types")
    @PreAuthorize("hasAnyRole('PROF', 'STUDENT')")
    public ResponseEntity<ApiResponseDTO<Grade.GradeType[]>> getGradeTypes() {
        try {
            Grade.GradeType[] gradeTypes = Grade.GradeType.values();
            return ResponseEntity.ok(ApiResponseDTO.success(gradeTypes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des types de notes: " + e.getMessage()));
        }
    }

    /**
     * Get all students (for professors to select when creating grades/remarks)
     */
    @GetMapping("/students")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<List<Student>>> getAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            return ResponseEntity.ok(ApiResponseDTO.success(students));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des étudiants: " + e.getMessage()));
        }
    }

    /**
     * Get dashboard statistics for professors
     */
    @GetMapping("/professor-stats")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getProfessorStats(Authentication authentication) {
        try {
            String email = authentication.getName();
            Prof prof = profService.getProfByEmail(email);
            
            // Get statistics
            List<Grade> myGrades = gradeService.getGradesByProfId(prof.getId());
            List<ProfessorNoteDTO> myNotes = professorNoteService.getNotesByProfessor(email);
            List<ProfCourseNiveau> myProfCourseNiveaux = profCourseNiveauService.getProfCourseNiveauxByProfId(prof.getId());
            
            // Calculate stats
            long totalGrades = myGrades.size();
            long totalRemarks = myNotes.size();
            long totalCourses = myProfCourseNiveaux.size();
            long visibleRemarks = myNotes.stream().filter(note -> note.isVisibleToStudent()).count();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalGrades", totalGrades);
            stats.put("totalRemarks", totalRemarks);
            stats.put("totalCourses", totalCourses);
            stats.put("visibleRemarks", visibleRemarks);
            stats.put("hiddenRemarks", totalRemarks - visibleRemarks);
            
            return ResponseEntity.ok(ApiResponseDTO.success(stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des statistiques: " + e.getMessage()));
        }
    }

    /**
     * Get dashboard statistics for students
     */
    @GetMapping("/student-stats")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getStudentStats(Authentication authentication) {
        try {
            String email = authentication.getName();
            Student student = studentService.getStudentByEmail(email);
            
            // Get statistics
            List<Grade> allGrades = gradeService.getGradesByStudentId(student.getId());
            List<ProfessorNoteDTO> professorNotes = professorNoteService.getNotesForStudent(email);
            
            // Group grades by prof-course-niveau to calculate subjects
            Map<Long, List<Grade>> gradesByProfCourseNiveau = allGrades.stream()
                .collect(Collectors.groupingBy(grade -> grade.getProfCourseNiveau().getId()));
            
            long totalSubjects = gradesByProfCourseNiveau.size();
            long totalGrades = allGrades.size();
            long totalRemarks = professorNotes.size();
            
            // Calculate passed/failed subjects
            long passedSubjects = 0;
            long failedSubjects = 0;
            long inProgressSubjects = 0;
            
            for (List<Grade> grades : gradesByProfCourseNiveau.values()) {
                Double dsNote = null;
                Double examenNote = null;
                
                for (Grade grade : grades) {
                    if (grade.getGradeType() == Grade.GradeType.DS) {
                        dsNote = grade.getNote();
                    } else if (grade.getGradeType() == Grade.GradeType.EXAMEN) {
                        examenNote = grade.getNote();
                    }
                }
                
                if (dsNote != null && examenNote != null) {
                    double moyenne = (dsNote * 0.3) + (examenNote * 0.7);
                    if (moyenne >= 10) {
                        passedSubjects++;
                    } else {
                        failedSubjects++;
                    }
                } else {
                    inProgressSubjects++;
                }
            }
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalSubjects", totalSubjects);
            stats.put("totalGrades", totalGrades);
            stats.put("totalRemarks", totalRemarks);
            stats.put("passedSubjects", passedSubjects);
            stats.put("failedSubjects", failedSubjects);
            stats.put("inProgressSubjects", inProgressSubjects);
            
            return ResponseEntity.ok(ApiResponseDTO.success(stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des statistiques: " + e.getMessage()));
        }
    }
}