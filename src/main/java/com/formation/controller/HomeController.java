package com.formation.controller;

import com.formation.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Accueil - Centre de Formation");
        model.addAttribute("totalStudents", studentService.countStudents());
        return "home";
    }
}