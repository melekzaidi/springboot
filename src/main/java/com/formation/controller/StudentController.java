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
        return "students/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Student student = new Student();
        student.setDateInscription(LocalDate.now());

        model.addAttribute("student", student);
        model.addAttribute("pageTitle", "Ajouter un nouvel étudiant");
        return "students/form";
    }

    @PostMapping("/save")
    public String saveStudent(@Valid @ModelAttribute Student student,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Ajouter un nouvel étudiant");
            return "students/form";
        }

        if (!studentService.isMatriculeUnique(student.getMatricule())) {
            result.rejectValue("matricule", "error.student", "Ce matricule existe déjà!");
            model.addAttribute("pageTitle", "Ajouter un nouvel étudiant");
            return "students/form";
        }

        if (!studentService.isEmailUnique(student.getEmail())) {
            result.rejectValue("email", "error.student", "Cet email existe déjà!");
            model.addAttribute("pageTitle", "Ajouter un nouvel étudiant");
            return "students/form";
        }

        studentService.saveStudent(student);

        redirectAttributes.addFlashAttribute("successMessage",
                "Étudiant ajouté avec succès!");

        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Student student = studentService.getStudentById(id);
            model.addAttribute("student", student);
            model.addAttribute("pageTitle", "Modifier l'étudiant: " + student.getNom());
            return "students/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/students";
        }
    }

    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable Long id,
                                @Valid @ModelAttribute Student student,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Modifier l'étudiant");
            return "students/form";
        }

        Student existingStudent = studentService.getStudentById(id);

        if (!existingStudent.getMatricule().equals(student.getMatricule()) &&
                !studentService.isMatriculeUnique(student.getMatricule())) {
            result.rejectValue("matricule", "error.student", "Ce matricule existe déjà!");
            model.addAttribute("pageTitle", "Modifier l'étudiant");
            return "students/form";
        }

        if (!existingStudent.getEmail().equals(student.getEmail()) &&
                !studentService.isEmailUnique(student.getEmail())) {
            result.rejectValue("email", "error.student", "Cet email existe déjà!");
            model.addAttribute("pageTitle", "Modifier l'étudiant");
            return "students/form";
        }

        studentService.updateStudent(id, student);

        redirectAttributes.addFlashAttribute("successMessage",
                "Étudiant modifié avec succès!");

        return "redirect:/students";
    }

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

    @GetMapping("/view/{id}")
    public String viewStudent(@PathVariable Long id, Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            Student student = studentService.getStudentById(id);
            model.addAttribute("student", student);
            model.addAttribute("pageTitle", "Détails de l'étudiant: " + student.getNom());
            return "students/view";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/students";
        }
    }
}