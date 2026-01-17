package com.formation.controller;

import com.formation.entity.Niveau;
import com.formation.entity.Student;
import com.formation.entity.StudentNiveau;
import com.formation.service.NiveauService;
import com.formation.service.StudentNiveauService;
import com.formation.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student-niveaux")
public class StudentNiveauController {

    @Autowired
    private StudentNiveauService studentNiveauService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private NiveauService niveauService;

    @GetMapping
    public String listAssignments(Model model) {
        List<StudentNiveau> assignments = studentNiveauService.getAllStudentNiveaux();
        model.addAttribute("assignments", assignments);
        model.addAttribute("pageTitle", "Affectation des Étudiants aux Niveaux");
        return "student-niveaux/list";
    }

    @GetMapping("/assign")
    public String showAssignForm(Model model) {
        List<Student> students = studentService.getAllStudents();
        List<Niveau> niveaux = niveauService.getAllNiveaux();

        model.addAttribute("students", students);
        model.addAttribute("niveaux", niveaux);
        model.addAttribute("studentNiveau", new StudentNiveau());
        model.addAttribute("pageTitle", "Affecter un étudiant à un niveau");
        return "student-niveaux/form";
    }

    @PostMapping("/assign")
    public String assignStudentToNiveau(@ModelAttribute StudentNiveau studentNiveau,
                                        RedirectAttributes redirectAttributes) {

        // Check if already assigned
        if (studentNiveauService.existsByStudentAndNiveau(
                studentNiveau.getStudent(), studentNiveau.getNiveau())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cet étudiant est déjà affecté à ce niveau!");
            return "redirect:/student-niveaux/assign";
        }

        studentNiveauService.saveStudentNiveau(studentNiveau);
        redirectAttributes.addFlashAttribute("successMessage",
                "Étudiant affecté au niveau avec succès!");
        return "redirect:/student-niveaux";
    }

    @GetMapping("/delete/{id}")
    public String deleteAssignment(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            studentNiveauService.deleteStudentNiveau(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Affectation supprimée avec succès!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/student-niveaux";
    }

    @GetMapping("/by-student/{studentId}")
    public String getAssignmentsByStudent(@PathVariable Long studentId, Model model,
                                          RedirectAttributes redirectAttributes) {
        try {
            Student student = studentService.getStudentById(studentId);
            List<StudentNiveau> assignments = studentNiveauService.getByStudent(student);
            model.addAttribute("student", student);
            model.addAttribute("assignments", assignments);
            model.addAttribute("pageTitle", "Niveaux de l'étudiant: " + student.getNom());
            return "student-niveaux/by-student";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/students";
        }
    }

    @GetMapping("/by-niveau/{niveauId}")
    public String getAssignmentsByNiveau(@PathVariable Long niveauId, Model model,
                                         RedirectAttributes redirectAttributes) {
        try {
            Niveau niveau = niveauService.getNiveauById(niveauId);
            List<StudentNiveau> assignments = studentNiveauService.getByNiveau(niveau);
            model.addAttribute("niveau", niveau);
            model.addAttribute("assignments", assignments);
            model.addAttribute("pageTitle", "Étudiants du niveau: " + niveau.getNom());
            return "student-niveaux/by-niveau";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/niveaux";
        }
    }
}