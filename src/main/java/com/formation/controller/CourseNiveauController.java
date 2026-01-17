package com.formation.controller;

import com.formation.entity.Course;
import com.formation.entity.CourseNiveau;
import com.formation.entity.Niveau;
import com.formation.service.CourseNiveauService;
import com.formation.service.CourseService;
import com.formation.service.NiveauService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/course-niveaux")
public class CourseNiveauController {

    @Autowired
    private CourseNiveauService courseNiveauService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private NiveauService niveauService;

    @GetMapping
    public String listCourseNiveaux(Model model) {
        List<CourseNiveau> courseNiveaux = courseNiveauService.getAllCourseNiveaux();
        model.addAttribute("courseNiveaux", courseNiveaux);
        model.addAttribute("pageTitle", "Gestion des Cours par Niveau");
        return "course-niveaux/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        CourseNiveau courseNiveau = new CourseNiveau();
        List<Course> courses = courseService.getAllCourses();
        List<Niveau> niveaux = niveauService.getAllNiveaux();

        model.addAttribute("courseNiveau", courseNiveau);
        model.addAttribute("courses", courses);
        model.addAttribute("niveaux", niveaux);
        model.addAttribute("pageTitle", "Associer un cours à un niveau");
        return "course-niveaux/form";
    }

    @PostMapping("/save")
    public String saveCourseNiveau(@Valid @ModelAttribute CourseNiveau courseNiveau,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            List<Course> courses = courseService.getAllCourses();
            List<Niveau> niveaux = niveauService.getAllNiveaux();
            model.addAttribute("courses", courses);
            model.addAttribute("niveaux", niveaux);
            model.addAttribute("pageTitle", "Associer un cours à un niveau");
            return "course-niveaux/form";
        }

        // Check if association already exists
        if (courseNiveauService.existsByCourseAndNiveau(courseNiveau.getCourse(), courseNiveau.getNiveau())) {
            result.rejectValue("course", "error.courseNiveau", "Cette association existe déjà!");
            List<Course> courses = courseService.getAllCourses();
            List<Niveau> niveaux = niveauService.getAllNiveaux();
            model.addAttribute("courses", courses);
            model.addAttribute("niveaux", niveaux);
            model.addAttribute("pageTitle", "Associer un cours à un niveau");
            return "course-niveaux/form";
        }

        courseNiveauService.saveCourseNiveau(courseNiveau);
        redirectAttributes.addFlashAttribute("successMessage", "Association ajoutée avec succès!");
        return "redirect:/course-niveaux";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            CourseNiveau courseNiveau = courseNiveauService.getCourseNiveauById(id);
            List<Course> courses = courseService.getAllCourses();
            List<Niveau> niveaux = niveauService.getAllNiveaux();

            model.addAttribute("courseNiveau", courseNiveau);
            model.addAttribute("courses", courses);
            model.addAttribute("niveaux", niveaux);
            model.addAttribute("pageTitle", "Modifier l'association Cours-Niveau");
            return "course-niveaux/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/course-niveaux";
        }
    }

    @PostMapping("/update/{id}")
    public String updateCourseNiveau(@PathVariable Long id,
                                     @Valid @ModelAttribute CourseNiveau courseNiveau,
                                     BindingResult result,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            List<Course> courses = courseService.getAllCourses();
            List<Niveau> niveaux = niveauService.getAllNiveaux();
            model.addAttribute("courses", courses);
            model.addAttribute("niveaux", niveaux);
            model.addAttribute("pageTitle", "Modifier l'association Cours-Niveau");
            return "course-niveaux/form";
        }

        CourseNiveau existingCourseNiveau = courseNiveauService.getCourseNiveauById(id);

        // Check if new association already exists (excluding current)
        if (!existingCourseNiveau.getCourse().equals(courseNiveau.getCourse()) ||
                !existingCourseNiveau.getNiveau().equals(courseNiveau.getNiveau())) {
            if (courseNiveauService.existsByCourseAndNiveau(courseNiveau.getCourse(), courseNiveau.getNiveau())) {
                result.rejectValue("course", "error.courseNiveau", "Cette association existe déjà!");
                List<Course> courses = courseService.getAllCourses();
                List<Niveau> niveaux = niveauService.getAllNiveaux();
                model.addAttribute("courses", courses);
                model.addAttribute("niveaux", niveaux);
                model.addAttribute("pageTitle", "Modifier l'association Cours-Niveau");
                return "course-niveaux/form";
            }
        }

        courseNiveauService.updateCourseNiveau(id, courseNiveau);
        redirectAttributes.addFlashAttribute("successMessage", "Association modifiée avec succès!");
        return "redirect:/course-niveaux";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourseNiveau(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            courseNiveauService.deleteCourseNiveau(id);
            redirectAttributes.addFlashAttribute("successMessage", "Association supprimée avec succès!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/course-niveaux";
    }
}