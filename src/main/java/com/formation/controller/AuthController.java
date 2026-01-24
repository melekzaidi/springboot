package com.formation.controller;

import com.formation.entity.Prof;
import com.formation.entity.Student;
import com.formation.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("email") String email, @RequestParam("role") String role, HttpSession session, Model model) {
        if ("student".equalsIgnoreCase(role)) {
            Student student = authService.loginStudent(email);
            if (student != null) {
                session.setAttribute("userType", "student");
                session.setAttribute("user", student);
                return "redirect:/calendar";
            }
        } else if ("prof".equalsIgnoreCase(role)) {
            Prof prof = authService.loginProf(email);
            if (prof != null) {
                session.setAttribute("userType", "prof");
                session.setAttribute("user", prof);
                return "redirect:/calendar";
            }
        }
        
        model.addAttribute("error", "Email invalide ou utilisateur non trouvé");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
