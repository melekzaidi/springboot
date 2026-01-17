package com.formation.controller;

import com.formation.entity.Course;
import com.formation.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public String listCourses(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        model.addAttribute("pageTitle", "Gestion des Cours");
        return "courses/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("pageTitle", "Ajouter un nouveau cours");
        return "courses/form";
    }

    @PostMapping("/save")
    public String saveCourse(@Valid @ModelAttribute Course course,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Ajouter un nouveau cours");
            return "courses/form";
        }

        if (!courseService.isCodeUnique(course.getCode())) {
            result.rejectValue("code", "error.course", "Ce code existe déjà!");
            model.addAttribute("pageTitle", "Ajouter un nouveau cours");
            return "courses/form";
        }

        courseService.saveCourse(course);
        redirectAttributes.addFlashAttribute("successMessage", "Cours ajouté avec succès!");
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Course course = courseService.getCourseById(id);
            model.addAttribute("course", course);
            model.addAttribute("pageTitle", "Modifier le cours: " + course.getNom());
            return "courses/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/courses";
        }
    }

    @PostMapping("/update/{id}")
    public String updateCourse(@PathVariable Long id,
                               @Valid @ModelAttribute Course course,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Modifier le cours");
            return "courses/form";
        }

        Course existingCourse = courseService.getCourseById(id);

        if (!existingCourse.getCode().equals(course.getCode()) &&
                !courseService.isCodeUnique(course.getCode())) {
            result.rejectValue("code", "error.course", "Ce code existe déjà!");
            model.addAttribute("pageTitle", "Modifier le cours");
            return "courses/form";
        }

        courseService.updateCourse(id, course);
        redirectAttributes.addFlashAttribute("successMessage", "Cours modifié avec succès!");
        return "redirect:/courses";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cours supprimé avec succès!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/courses";
    }
}