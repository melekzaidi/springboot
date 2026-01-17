package com.formation.controller;

import com.formation.entity.Student;
import com.formation.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // Display list of students
    @GetMapping
    public String listStudents(Model model,
                               @RequestParam(value = "keyword", required = false) String keyword) {
        List<Student> students;

        if (keyword != null && !keyword.isEmpty()) {
            students = studentService.searchStudents(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            students = studentService.getAllStudents();
        }

        model.addAttribute("students", students);
        model.addAttribute("totalStudents", studentService.countStudents());
        model.addAttribute("pageTitle", "Gestion des Étudiants");
        return "students/list";  // Changed: return direct template
    }

    // Show form to create new student
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Student student = new Student();
        student.setDateInscription(LocalDate.now()); // Default to today

        model.addAttribute("student", student);
        model.addAttribute("pageTitle", "Ajouter un nouvel étudiant");
        return "students/form";  // Changed: return direct template
    }

    // Handle form submission for new student
    @PostMapping("/save")
    public String saveStudent(@Valid @ModelAttribute Student student,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        // Check for validation errors
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Ajouter un nouvel étudiant");
            return "students/form";
        }

        // Check if matricule is unique
        if (!studentService.isMatriculeUnique(student.getMatricule())) {
            result.rejectValue("matricule", "error.student", "Ce matricule existe déjà!");
            model.addAttribute("pageTitle", "Ajouter un nouvel étudiant");
            return "students/form";
        }

        // Check if email is unique
        if (!studentService.isEmailUnique(student.getEmail())) {
            result.rejectValue("email", "error.student", "Cet email existe déjà!");
            model.addAttribute("pageTitle", "Ajouter un nouvel étudiant");
            return "students/form";
        }

        // Save student
        studentService.saveStudent(student);

        redirectAttributes.addFlashAttribute("successMessage",
                "Étudiant ajouté avec succès!");

        return "redirect:/students";
    }

    // Show form to edit existing student
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Student student = studentService.getStudentById(id);
            model.addAttribute("student", student);
            model.addAttribute("pageTitle", "Modifier l'étudiant: " + student   .getNom());
            return "students/form";  // Changed: return direct template
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/students";
        }
    }

    // Handle form submission for updating student
    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable Long id,
                                @Valid @ModelAttribute Student student,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        // Check for validation errors
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Modifier l'étudiant");
            return "students/form";
        }

        // Get existing student
        Student existingStudent = studentService.getStudentById(id);

        // Check if matricule is unique (excluding current student)
        if (!existingStudent.getMatricule().equals(student.getMatricule()) &&
                !studentService.isMatriculeUnique(student.getMatricule())) {
            result.rejectValue("matricule", "error.student", "Ce matricule existe déjà!");
            model.addAttribute("pageTitle", "Modifier l'étudiant");
            return "students/form";
        }

        // Check if email is unique (excluding current student)
        if (!existingStudent.getEmail().equals(student.getEmail()) &&
                !studentService.isEmailUnique(student.getEmail())) {
            result.rejectValue("email", "error.student", "Cet email existe déjà!");
            model.addAttribute("pageTitle", "Modifier l'étudiant");
            return "students/form";
        }

        // Update student
        studentService.updateStudent(id, student);

        redirectAttributes.addFlashAttribute("successMessage",
                "Étudiant modifié avec succès!");

        return "redirect:/students";
    }

    // Delete student
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Étudiant supprimé avec succès!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erreur lors de la suppression: " + e.getMessage());
        }

        return "redirect:/students";
    }

    // View student details
    @GetMapping("/view/{id}")
    public String viewStudent(@PathVariable Long id, Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            Student student = studentService.getStudentById(id);
            model.addAttribute("student", student);
            model.addAttribute("pageTitle", "Détails de l'étudiant: " + student.getNom());
            return "students/view";  // Changed: return direct template
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/students";
        }
    }
}