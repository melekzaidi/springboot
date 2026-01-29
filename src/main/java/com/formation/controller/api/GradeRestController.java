package com.formation.controller.api;

import com.formation.dto.ApiResponseDTO;
import com.formation.dto.GradeDTO;
import com.formation.dto.StudentGradesSummaryDTO;
import com.formation.entity.Grade;
import com.formation.entity.ProfCourseNiveau;
import com.formation.entity.Student;
import com.formation.entity.Prof;
import com.formation.mapper.GradeMapper;
import com.formation.service.GradeService;
import com.formation.service.ProfCourseNiveauService;
import com.formation.service.StudentService;
import com.formation.service.ProfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grades")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class GradeRestController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ProfCourseNiveauService profCourseNiveauService;

    @Autowired
    private ProfService profService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<GradeDTO>>> getAllGrades() {
        try {
            List<Grade> grades = gradeService.getAllGrades();
            List<GradeDTO> gradeDTOs = gradeMapper.toDTOList(grades);
            return ResponseEntity.ok(ApiResponseDTO.success(gradeDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des notes: " + e.getMessage()));
        }
    }

    @GetMapping("/grade-types")
    public ResponseEntity<ApiResponseDTO<Grade.GradeType[]>> getGradeTypes() {
        try {
            Grade.GradeType[] gradeTypes = Grade.GradeType.values();
            return ResponseEntity.ok(ApiResponseDTO.success(gradeTypes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des types de notes: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<GradeDTO>> getGradeById(@PathVariable Long id) {
        try {
            Grade grade = gradeService.getGradeById(id);
            GradeDTO gradeDTO = gradeMapper.toDTO(grade);
            return ResponseEntity.ok(ApiResponseDTO.success(gradeDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<GradeDTO>> createGrade(@Valid @RequestBody GradeDTO gradeDTO) {
        try {
            // Get the Student and ProfCourseNiveau entities
            Student student = studentService.getStudentById(gradeDTO.getStudentId());
            ProfCourseNiveau profCourseNiveau = profCourseNiveauService.getProfCourseNiveauById(gradeDTO.getProfCourseNiveauId());
            
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

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<GradeDTO>> updateGrade(
            @PathVariable Long id, 
            @Valid @RequestBody GradeDTO gradeDTO) {
        try {
            Grade existingGrade = gradeService.getGradeById(id);
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<String>> deleteGrade(@PathVariable Long id) {
        try {
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

    @GetMapping("/my-grades")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<List<GradeDTO>>> getMyGrades(Authentication authentication) {
        try {
            String email = authentication.getName();
            Prof prof = profService.getProfByEmail(email);
            List<Grade> grades = gradeService.getGradesByProfId(prof.getId());
            List<GradeDTO> gradeDTOs = gradeMapper.toDTOList(grades);
            return ResponseEntity.ok(ApiResponseDTO.success(gradeDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération de vos notes: " + e.getMessage()));
        }
    }

    @GetMapping("/my-prof-course-niveaux")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<List<ProfCourseNiveau>>> getMyProfCourseNiveaux(Authentication authentication) {
        try {
            String email = authentication.getName();
            Prof prof = profService.getProfByEmail(email);
            List<ProfCourseNiveau> profCourseNiveaux = profCourseNiveauService.getProfCourseNiveauxByProfId(prof.getId());
            return ResponseEntity.ok(ApiResponseDTO.success(profCourseNiveaux));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération de vos affectations: " + e.getMessage()));
        }
    }

    @PostMapping("/for-prof")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<GradeDTO>> createGradeForProf(@Valid @RequestBody GradeDTO gradeDTO, Authentication authentication) {
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

    @GetMapping("/summary/my-prof-course-niveau/{profCourseNiveauId}")
    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<ApiResponseDTO<List<StudentGradesSummaryDTO>>> getMyStudentGradesSummary(
            @PathVariable Long profCourseNiveauId, Authentication authentication) {
        try {
            String email = authentication.getName();
            Prof prof = profService.getProfByEmail(email);
            
            // Verify that the prof-course-niveau belongs to the authenticated professor
            ProfCourseNiveau profCourseNiveau = profCourseNiveauService.getProfCourseNiveauById(profCourseNiveauId);
            if (!profCourseNiveau.getProf().getId().equals(prof.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseDTO.error("Vous n'êtes pas autorisé à voir les notes de cette matière"));
            }
            
            List<StudentGradesSummaryDTO> summaries = gradeService.getStudentGradesSummaryByProfCourseNiveau(profCourseNiveauId);
            return ResponseEntity.ok(ApiResponseDTO.success(summaries));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération du résumé des notes: " + e.getMessage()));
        }
    }

    @GetMapping("/by-student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF', 'STUDENT')")
    public ResponseEntity<ApiResponseDTO<List<GradeDTO>>> getGradesByStudent(@PathVariable Long studentId) {
        try {
            List<Grade> grades = gradeService.getGradesByStudentId(studentId);
            List<GradeDTO> gradeDTOs = gradeMapper.toDTOList(grades);
            return ResponseEntity.ok(ApiResponseDTO.success(gradeDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des notes de l'étudiant: " + e.getMessage()));
        }
    }

    @GetMapping("/by-prof-course-niveau/{profCourseNiveauId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<List<GradeDTO>>> getGradesByProfCourseNiveau(@PathVariable Long profCourseNiveauId) {
        try {
            List<Grade> grades = gradeService.getGradesByProfCourseNiveauId(profCourseNiveauId);
            List<GradeDTO> gradeDTOs = gradeMapper.toDTOList(grades);
            return ResponseEntity.ok(ApiResponseDTO.success(gradeDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des notes pour cette affectation: " + e.getMessage()));
        }
    }

    @GetMapping("/summary/by-prof-course-niveau/{profCourseNiveauId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<List<StudentGradesSummaryDTO>>> getStudentGradesSummary(@PathVariable Long profCourseNiveauId) {
        try {
            List<StudentGradesSummaryDTO> summaries = gradeService.getStudentGradesSummaryByProfCourseNiveau(profCourseNiveauId);
            return ResponseEntity.ok(ApiResponseDTO.success(summaries));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération du résumé des notes: " + e.getMessage()));
        }
    }

    @GetMapping("/moyenne/{studentId}/{profCourseNiveauId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF', 'STUDENT')")
    public ResponseEntity<ApiResponseDTO<Double>> getStudentMoyenne(
            @PathVariable Long studentId, 
            @PathVariable Long profCourseNiveauId) {
        try {
            Double moyenne = gradeService.calculateMoyenneForStudent(studentId, profCourseNiveauId);
            return ResponseEntity.ok(ApiResponseDTO.success(moyenne));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors du calcul de la moyenne: " + e.getMessage()));
        }
    }

    @GetMapping("/my-grades-as-student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponseDTO<List<GradeDTO>>> getMyGradesAsStudent(Authentication authentication) {
        try {
            String email = authentication.getName();
            // We need to get student by email - let's add this method to StudentService
            Student student = studentService.getStudentByEmail(email);
            List<Grade> grades = gradeService.getGradesByStudentId(student.getId());
            List<GradeDTO> gradeDTOs = gradeMapper.toDTOList(grades);
            return ResponseEntity.ok(ApiResponseDTO.success(gradeDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération de vos notes: " + e.getMessage()));
        }
    }

    @GetMapping("/my-summary")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponseDTO<List<StudentGradesSummaryDTO>>> getMyGradesSummary(Authentication authentication) {
        try {
            String email = authentication.getName();
            Student student = studentService.getStudentByEmail(email);
            
            // Get all grades for this student grouped by prof-course-niveau
            List<Grade> allGrades = gradeService.getGradesByStudentId(student.getId());
            
            // Group by prof-course-niveau and create summaries
            Map<Long, List<Grade>> gradesByProfCourseNiveau = allGrades.stream()
                .collect(Collectors.groupingBy(grade -> grade.getProfCourseNiveau().getId()));
            
            List<StudentGradesSummaryDTO> summaries = new ArrayList<>();
            
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
                    summaries.add(summary);
                }
            }
            
            return ResponseEntity.ok(ApiResponseDTO.success(summaries));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération de vos notes: " + e.getMessage()));
        }
    }
}