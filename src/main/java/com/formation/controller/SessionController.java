package com.formation.controller;

import com.formation.entity.Prof;
import com.formation.entity.ProfCourseNiveau;
import com.formation.entity.Session;
import com.formation.service.CalendarService;
import com.formation.service.ProfCourseNiveauService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

import java.util.List;

@Controller
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private ProfCourseNiveauService profCourseNiveauService;

    @GetMapping
    public String listSessions(Model model) {
        // Since we want to show all sessions in a list format
        // We might need a method in CalendarService to get all sessions
        // For now, let's assume we can get them (we might need to add findAll to service)
        // If not available, we can rely on repository through service
        // Let's check CalendarService... it has getSessionsForStudent/Prof but not findAll.
        // I will need to update CalendarService to add getAllSessions()
        
        // Temporarily handling this inside controller for flow, but will update service next step
        // For now, I'll pass an empty list or redirect if service support is missing. 
        // Better: I will update the service in the next tool call. 
        // So here I will assume calendarService.getAllSessions() exists.
        
        // Wait, I can't call a method that doesn't exist.
        // I should update CalendarService FIRST.
        // But the user rule says "do not do parallel tool calls".
        // Use 'replace_file_content' to update service first? No, I am editing controller now.
        // I will leave the listSessions empty for a moment or use a repository if I access it?
        // No, best practice is Service.
        
        // Let's write the controller assuming the service method exists, then immediately update the service.
        List<Session> sessions = calendarService.getAllSessions();
        model.addAttribute("sessions", sessions);
        model.addAttribute("pageTitle", "Gestion des Séances");
        return "sessions/list-with-layout";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, org.springframework.security.core.Authentication authentication) {
        model.addAttribute("courseSession", new Session());
        
        List<ProfCourseNiveau> assignments;
        String role = authentication.getAuthorities().stream().findFirst().get().getAuthority();
        
        if ("ROLE_PROF".equals(role)) {
             String email = authentication.getName();
             // We need a method to get assignments for a specfic Prof email or ID
             // profCourseNiveauService.getAssignmentsByProfEmail(email);
             // Since I can't easily change Service right now without reading it, I'll iterate or assuming I added such method?
             // Or find prof by email then filter.
             // Let's rely on repository directly or service if possible.
             // I'll fetch all and filter in memory for now to be safe, or add method if I can.
             // Better: find prof then filter.
             assignments = profCourseNiveauService.getAllProfCourseNiveaux().stream()
                 .filter(pcn -> pcn.getProf().getEmail().equals(email))
                 .collect(java.util.stream.Collectors.toList());
        } else {
             assignments = profCourseNiveauService.getAllProfCourseNiveaux();
        }
        
        model.addAttribute("assignments", assignments);
        model.addAttribute("pageTitle", "Ajouter une nouvelle séance");
        return "sessions/form";
    }

    @PostMapping("/save")
    public String saveSession(@Valid @ModelAttribute("courseSession") Session sessionEntity,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("assignments", profCourseNiveauService.getAllProfCourseNiveaux());
            model.addAttribute("pageTitle", "Ajouter une nouvelle séance");
            return "sessions/form";
        }

        // We need to ensure the ProfCourseNiveau is loaded correctly
        ProfCourseNiveau assignment = profCourseNiveauService.getProfCourseNiveauById(sessionEntity.getProfCourseNiveau().getId());
        sessionEntity.setProfCourseNiveau(assignment);

        calendarService.saveSession(sessionEntity);
        redirectAttributes.addFlashAttribute("successMessage", "Séance enregistrée avec succès!");
        return "redirect:/sessions";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Session sessionEntity = calendarService.getSessionById(id);
            model.addAttribute("courseSession", sessionEntity);
            model.addAttribute("assignments", profCourseNiveauService.getAllProfCourseNiveaux());
            model.addAttribute("pageTitle", "Modifier la séance");
            return "sessions/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/sessions";
        }
    }

    @PostMapping("/update/{id}")
    public String updateSession(@PathVariable Long id,
                                @Valid @ModelAttribute("courseSession") Session sessionEntity,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
             model.addAttribute("assignments", profCourseNiveauService.getAllProfCourseNiveaux());
             model.addAttribute("pageTitle", "Modifier la séance");
             return "sessions/form";
        }

        Session existingSession = calendarService.getSessionById(id);
        
        existingSession.setStartDateTime(sessionEntity.getStartDateTime());
        existingSession.setEndDateTime(sessionEntity.getEndDateTime());
        existingSession.setDescription(sessionEntity.getDescription());
        
        ProfCourseNiveau newAssignment = profCourseNiveauService.getProfCourseNiveauById(sessionEntity.getProfCourseNiveau().getId());
        existingSession.setProfCourseNiveau(newAssignment);

        calendarService.saveSession(existingSession);
        
        redirectAttributes.addFlashAttribute("successMessage", "Séance modifiée avec succès!");
        return "redirect:/sessions";
    }

    @GetMapping("/delete/{id}")
    public String deleteSession(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
             calendarService.deleteSession(id);
             redirectAttributes.addFlashAttribute("successMessage", "Séance supprimée avec succès!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression: " + e.getMessage());
        }

        return "redirect:/sessions";
    }
}
