package com.formation.controller;

import com.formation.entity.CourseNiveau;
import com.formation.entity.Prof;
import com.formation.entity.ProfCourseNiveau;
import com.formation.service.CourseNiveauService;
import com.formation.service.ProfCourseNiveauService;
import com.formation.service.ProfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/prof-assignments")
public class ProfCourseNiveauController {

    @Autowired
    private ProfCourseNiveauService profCourseNiveauService;

    @Autowired
    private ProfService profService;

    @Autowired
    private CourseNiveauService courseNiveauService;

    @GetMapping
    public String listAssignments(Model model) {
        List<ProfCourseNiveau> assignments = profCourseNiveauService.getAllProfCourseNiveaux();
        model.addAttribute("assignments", assignments);
        model.addAttribute("pageTitle", "Affectation des Professeurs aux Cours");
        return "prof-assignments/list";
    }

    @GetMapping("/assign")
    public String showAssignForm(Model model) {
        List<Prof> profs = profService.getAllProfs();
        List<CourseNiveau> courseNiveaux = courseNiveauService.getAllCourseNiveaux();

        model.addAttribute("profs", profs);
        model.addAttribute("courseNiveaux", courseNiveaux);
        model.addAttribute("assignment", new ProfCourseNiveau());
        model.addAttribute("pageTitle", "Affecter un professeur à un cours");
        return "prof-assignments/form";
    }

    @PostMapping("/assign")
    public String assignProfToCourse(@ModelAttribute ProfCourseNiveau assignment,
                                     RedirectAttributes redirectAttributes) {

        // Check if already assigned
        if (profCourseNiveauService.existsByProfAndCourseNiveau(
                assignment.getProf(), assignment.getCourseNiveau())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ce professeur est déjà affecté à ce cours!");
            return "redirect:/prof-assignments/assign";
        }

        profCourseNiveauService.saveProfCourseNiveau(assignment);
        redirectAttributes.addFlashAttribute("successMessage",
                "Professeur affecté au cours avec succès!");
        return "redirect:/prof-assignments";
    }

    @GetMapping("/delete/{id}")
    public String deleteAssignment(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            profCourseNiveauService.deleteProfCourseNiveau(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Affectation supprimée avec succès!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/prof-assignments";
    }

    @GetMapping("/by-prof/{profId}")
    public String getAssignmentsByProf(@PathVariable Long profId, Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            Prof prof = profService.getProfById(profId);
            List<ProfCourseNiveau> assignments = profCourseNiveauService.getByProf(prof);
            model.addAttribute("prof", prof);
            model.addAttribute("assignments", assignments);
            model.addAttribute("pageTitle", "Cours du professeur: " + prof.getNomComplet());
            return "prof-assignments/by-prof";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profs";
        }
    }

    @GetMapping("/by-course-niveau/{courseNiveauId}")
    public String getAssignmentsByCourseNiveau(@PathVariable Long courseNiveauId, Model model,
                                               RedirectAttributes redirectAttributes) {
        try {
            CourseNiveau courseNiveau = courseNiveauService.getCourseNiveauById(courseNiveauId);
            List<ProfCourseNiveau> assignments = profCourseNiveauService.getByCourseNiveau(courseNiveau);
            model.addAttribute("courseNiveau", courseNiveau);
            model.addAttribute("assignments", assignments);
            model.addAttribute("pageTitle", "Professeurs du cours: " + courseNiveau.getCourse().getNom());
            return "prof-assignments/by-course-niveau";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/course-niveaux";
        }
    }
}