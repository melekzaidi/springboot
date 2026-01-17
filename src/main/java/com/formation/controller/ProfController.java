package com.formation.controller;

import com.formation.entity.Prof;
import com.formation.service.ProfService;
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
@RequestMapping("/profs")
public class ProfController {

    @Autowired
    private ProfService profService;

    @GetMapping
    public String listProfs(Model model,
                            @RequestParam(value = "keyword", required = false) String keyword) {
        List<Prof> profs = profService.getAllProfs();
        model.addAttribute("profs", profs);
        model.addAttribute("pageTitle", "Gestion des Professeurs");
        return "profs/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Prof prof = new Prof();
        prof.setDateEmbauche(LocalDate.now());
        model.addAttribute("prof", prof);
        model.addAttribute("pageTitle", "Ajouter un nouveau professeur");
        return "profs/form";
    }

    @PostMapping("/save")
    public String saveProf(@Valid @ModelAttribute Prof prof,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Ajouter un nouveau professeur");
            return "profs/form";
        }

        if (!profService.isMatriculeUnique(prof.getMatricule())) {
            result.rejectValue("matricule", "error.prof", "Ce matricule existe déjà!");
            model.addAttribute("pageTitle", "Ajouter un nouveau professeur");
            return "profs/form";
        }

        if (!profService.isEmailUnique(prof.getEmail())) {
            result.rejectValue("email", "error.prof", "Cet email existe déjà!");
            model.addAttribute("pageTitle", "Ajouter un nouveau professeur");
            return "profs/form";
        }

        profService.saveProf(prof);
        redirectAttributes.addFlashAttribute("successMessage", "Professeur ajouté avec succès!");
        return "redirect:/profs";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Prof prof = profService.getProfById(id);
            model.addAttribute("prof", prof);
            model.addAttribute("pageTitle", "Modifier le professeur: " + prof.getNomComplet());
            return "profs/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profs";
        }
    }

    @PostMapping("/update/{id}")
    public String updateProf(@PathVariable Long id,
                             @Valid @ModelAttribute Prof prof,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Modifier le professeur");
            return "profs/form";
        }

        Prof existingProf = profService.getProfById(id);

        if (!existingProf.getMatricule().equals(prof.getMatricule()) &&
                !profService.isMatriculeUnique(prof.getMatricule())) {
            result.rejectValue("matricule", "error.prof", "Ce matricule existe déjà!");
            model.addAttribute("pageTitle", "Modifier le professeur");
            return "profs/form";
        }

        if (!existingProf.getEmail().equals(prof.getEmail()) &&
                !profService.isEmailUnique(prof.getEmail())) {
            result.rejectValue("email", "error.prof", "Cet email existe déjà!");
            model.addAttribute("pageTitle", "Modifier le professeur");
            return "profs/form";
        }

        profService.updateProf(id, prof);
        redirectAttributes.addFlashAttribute("successMessage", "Professeur modifié avec succès!");
        return "redirect:/profs";
    }

    @GetMapping("/delete/{id}")
    public String deleteProf(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            profService.deleteProf(id);
            redirectAttributes.addFlashAttribute("successMessage", "Professeur supprimé avec succès!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/profs";
    }
}