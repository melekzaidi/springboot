package com.formation.controller;

import com.formation.service.ProfService;
import com.formation.service.StudentService;
import com.formation.repository.ProfRepository;
import com.formation.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProfRepository profRepository;

    @ModelAttribute("currentUser")
    public Object getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        String email = authentication.getName();
        String role = authentication.getAuthorities().stream().findFirst().get().getAuthority();

        if (role.equals("ROLE_STUDENT")) {
            return studentRepository.findByEmail(email).orElse(null);
        } else if (role.equals("ROLE_PROF")) {
            return profRepository.findByEmail(email).orElse(null);
        } else if (role.equals("ROLE_ADMIN")) {
            // Return a simple object or just null if admin has no profile
            // We can return a Map or a dummy object
             return new AdminProfile("Administrateur", "Système");
        }
        return null;
    }
    
    @ModelAttribute("currentRole")
    public String getCurrentRole() {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return authentication.getAuthorities().stream().findFirst().get().getAuthority().replace("ROLE_", "");
    }

    public static class AdminProfile {
        public String prenom;
        public String nom;
        public AdminProfile(String prenom, String nom) { this.prenom = prenom; this.nom = nom; }
    }
}
