package com.formation.controller;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/grades")
public class GradeController {

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
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public String listGrades(Model model, Authentication authentication) {
        try {
            List<ProfCourseNiveau> assignments;
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                // Admin can see all assignments
                assignments = profCourseNiveauService.getAllProfCourseNiveaux();
            } else {
                // Prof can only see their own assignments
                String email = authentication.getName();
                Prof prof = profService.getProfByEmail(email);
                assignments = profCourseNiveauService.getProfCourseNiveauxByProfId(prof.getId());
            }
            
            model.addAttribute("pageTitle", "Gestion des Notes");
            model.addAttribute("assignments", assignments);
            return "grades/list";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/my-assignments")
    @PreAuthorize("hasRole('PROF')")
    public String listMyAssignments(Model model, Authentication authentication) {
        try {
            String email = authentication.getName();
            Prof prof = profService.getProfByEmail(email);
            List<ProfCourseNiveau> profCourseNiveaux = profCourseNiveauService.getProfCourseNiveauxByProfId(prof.getId());
            
            model.addAttribute("pageTitle", "Mes Affectations - Notes");
            model.addAttribute("profCourseNiveaux", profCourseNiveaux);
            model.addAttribute("prof", prof);
            
            return "grades/my-assignments";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/by-prof-course-niveau/{profCourseNiveauId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public String listGradesByProfCourseNiveau(@PathVariable Long profCourseNiveauId, Model model, Authentication authentication) {
        try {
            ProfCourseNiveau profCourseNiveau = profCourseNiveauService.getProfCourseNiveauById(profCourseNiveauId);
            
            // Check if prof has access to this assignment
            if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                String email = authentication.getName();
                Prof prof = profService.getProfByEmail(email);
                if (!profCourseNiveau.getProf().getId().equals(prof.getId())) {
                    model.addAttribute("error", "Vous n'êtes pas autorisé à voir les notes de cette matière");
                    return "error";
                }
            }
            
            List<StudentGradesSummaryDTO> summaries = gradeService.getStudentGradesSummaryByProfCourseNiveau(profCourseNiveauId);
            
            String courseName = profCourseNiveau.getCourseNiveau().getCourse().getNom();
            String niveauName = profCourseNiveau.getCourseNiveau().getNiveau().getNom();
            String profName = profCourseNiveau.getProf().getPrenom() + " " + profCourseNiveau.getProf().getNom();
            
            model.addAttribute("pageTitle", "Notes - " + courseName + " - " + niveauName);
            model.addAttribute("summaries", summaries);
            model.addAttribute("profCourseNiveau", profCourseNiveau);
            model.addAttribute("courseName", courseName);
            model.addAttribute("niveauName", niveauName);
            model.addAttribute("profName", profName);
            
            return "grades/by-prof-course-niveau";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/form")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public String showGradeForm(@RequestParam(required = false) Long profCourseNiveauId,
                                @RequestParam(required = false) Long studentId,
                                Model model, Authentication authentication) {
        try {
            GradeDTO gradeDTO = new GradeDTO();
            
            // Set default date to today
            gradeDTO.setDateEvaluation(java.time.LocalDate.now());
            
            if (profCourseNiveauId != null) {
                gradeDTO.setProfCourseNiveauId(profCourseNiveauId);
                ProfCourseNiveau profCourseNiveau = profCourseNiveauService.getProfCourseNiveauById(profCourseNiveauId);
                
                // Check if prof has access to this assignment
                if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    String email = authentication.getName();
                    Prof prof = profService.getProfByEmail(email);
                    if (!profCourseNiveau.getProf().getId().equals(prof.getId())) {
                        model.addAttribute("error", "Vous n'êtes pas autorisé à noter dans cette matière");
                        return "error";
                    }
                }
                
                model.addAttribute("profCourseNiveau", profCourseNiveau);
                model.addAttribute("courseName", profCourseNiveau.getCourseNiveau().getCourse().getNom());
                model.addAttribute("niveauName", profCourseNiveau.getCourseNiveau().getNiveau().getNom());
                model.addAttribute("profName", profCourseNiveau.getProf().getPrenom() + " " + profCourseNiveau.getProf().getNom());
            }
            
            if (studentId != null) {
                gradeDTO.setStudentId(studentId);
            }
            
            model.addAttribute("pageTitle", "Nouvelle Note");
            model.addAttribute("gradeDTO", gradeDTO);
            model.addAttribute("gradeTypes", Grade.GradeType.values());
            model.addAttribute("students", studentService.getAllStudents());
            
            // Filter prof-course-niveaux based on user role
            List<ProfCourseNiveau> profCourseNiveaux;
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                profCourseNiveaux = profCourseNiveauService.getAllProfCourseNiveaux();
            } else {
                String email = authentication.getName();
                Prof prof = profService.getProfByEmail(email);
                profCourseNiveaux = profCourseNiveauService.getProfCourseNiveauxByProfId(prof.getId());
            }
            model.addAttribute("profCourseNiveaux", profCourseNiveaux);
            
            return "grades/form";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public String showEditGradeForm(@PathVariable Long id, Model model) {
        try {
            Grade grade = gradeService.getGradeById(id);
            GradeDTO gradeDTO = gradeMapper.toDTO(grade);
            
            model.addAttribute("pageTitle", "Modifier la Note");
            model.addAttribute("gradeDTO", gradeDTO);
            model.addAttribute("gradeTypes", Grade.GradeType.values());
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("profCourseNiveaux", profCourseNiveauService.getAllProfCourseNiveaux());
            model.addAttribute("isEdit", true);
            
            return "grades/form";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public String saveGrade(@Valid @ModelAttribute GradeDTO gradeDTO, 
                           BindingResult bindingResult, 
                           Model model, 
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("gradeTypes", Grade.GradeType.values());
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("profCourseNiveaux", profCourseNiveauService.getAllProfCourseNiveaux());
            return "grades/form";
        }

        try {
            Student student = studentService.getStudentById(gradeDTO.getStudentId());
            ProfCourseNiveau profCourseNiveau = profCourseNiveauService.getProfCourseNiveauById(gradeDTO.getProfCourseNiveauId());
            
            // Check if grade already exists for this type (only for new grades)
            if (gradeDTO.getId() == null && 
                gradeService.existsGradeForStudentAndProfCourseNiveauAndType(student, profCourseNiveau, gradeDTO.getGradeType())) {
                model.addAttribute("error", "Une note de type " + gradeDTO.getGradeType().getDisplayName() + 
                        " existe déjà pour cet étudiant dans cette matière");
                model.addAttribute("gradeTypes", Grade.GradeType.values());
                model.addAttribute("students", studentService.getAllStudents());
                model.addAttribute("profCourseNiveaux", profCourseNiveauService.getAllProfCourseNiveaux());
                return "grades/form";
            }
            
            // Set default coefficient if not provided
            if (gradeDTO.getCoefficient() == null) {
                gradeDTO.setCoefficient(gradeDTO.getGradeType().getDefaultCoefficient());
            }
            
            Grade grade;
            if (gradeDTO.getId() != null) {
                // Update existing grade
                grade = gradeService.getGradeById(gradeDTO.getId());
                gradeMapper.updateEntityFromDTO(gradeDTO, grade);
            } else {
                // Create new grade
                grade = gradeMapper.toEntity(gradeDTO, student, profCourseNiveau);
            }
            
            gradeService.saveGrade(grade);
            redirectAttributes.addFlashAttribute("success", "Note sauvegardée avec succès");
            
            return "redirect:/grades/by-prof-course-niveau/" + gradeDTO.getProfCourseNiveauId();
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("gradeTypes", Grade.GradeType.values());
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("profCourseNiveaux", profCourseNiveauService.getAllProfCourseNiveaux());
            return "grades/form";
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public String deleteGrade(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Grade grade = gradeService.getGradeById(id);
            Long profCourseNiveauId = grade.getProfCourseNiveau().getId();
            
            gradeService.deleteGrade(id);
            redirectAttributes.addFlashAttribute("success", "Note supprimée avec succès");
            
            return "redirect:/grades/by-prof-course-niveau/" + profCourseNiveauId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/grades";
        }
    }
}