package com.formation.controller;

import com.formation.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ProfService profService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private NiveauService niveauService;

    @Autowired
    private CourseNiveauService courseNiveauService;

    @Autowired
    private ProfCourseNiveauService profCourseNiveauService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Accueil - Centre de Formation");
        model.addAttribute("totalStudents", studentService.countStudents());
        model.addAttribute("totalProfs", profService.getAllProfs().size());
        model.addAttribute("totalCourses", courseService.getAllCourses().size());
        model.addAttribute("totalNiveaux", niveauService.getAllNiveaux().size());
        model.addAttribute("totalCourseNiveaux", courseNiveauService.getAllCourseNiveaux().size());
        model.addAttribute("totalAssignments", profCourseNiveauService.getAllProfCourseNiveaux().size());
        return "home";
    }
}