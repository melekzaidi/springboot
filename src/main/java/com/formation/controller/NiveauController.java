package com.formation.controller;

import com.formation.entity.Niveau;
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
@RequestMapping("/niveaux")
public class NiveauController {

    @Autowired
    private NiveauService niveauService;

    @GetMapping
    public String listNiveaux(Model model) {
        List<Niveau> niveaux = niveauService.getAllNiveaux();
        model.addAttribute("niveaux", niveaux);
        model.addAttribute("pageTitle", "Gestion des Niveaux");
        return "niveaux/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("niveau", new Niveau());
        model.addAttribute("pageTitle", "Ajouter un nouveau niveau");
        return "niveaux/form";
    }

    @PostMapping("/save")
    public String saveNiveau(@Valid @ModelAttribute Niveau niveau,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Ajouter un nouveau niveau");
            return "niveaux/form";
        }

        if (!niveauService.isCodeUnique(niveau.getCode())) {
            result.rejectValue("code", "error.niveau", "Ce code existe déjà!");
            model.addAttribute("pageTitle", "Ajouter un nouveau niveau");
            return "niveaux/form";
        }

        niveauService.saveNiveau(niveau);
        redirectAttributes.addFlashAttribute("successMessage", "Niveau ajouté avec succès!");
        return "redirect:/niveaux";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Niveau niveau = niveauService.getNiveauById(id);
            model.addAttribute("niveau", niveau);
            model.addAttribute("pageTitle", "Modifier le niveau: " + niveau.getNom());
            return "niveaux/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/niveaux";
        }
    }

    @PostMapping("/update/{id}")
    public String updateNiveau(@PathVariable Long id,
                               @Valid @ModelAttribute Niveau niveau,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Modifier le niveau");
            return "niveaux/form";
        }

        Niveau existingNiveau = niveauService.getNiveauById(id);

        if (!existingNiveau.getCode().equals(niveau.getCode()) &&
                !niveauService.isCodeUnique(niveau.getCode())) {
            result.rejectValue("code", "error.niveau", "Ce code existe déjà!");
            model.addAttribute("pageTitle", "Modifier le niveau");
            return "niveaux/form";
        }

        niveauService.updateNiveau(id, niveau);
        redirectAttributes.addFlashAttribute("successMessage", "Niveau modifié avec succès!");
        return "redirect:/niveaux";
    }

    @GetMapping("/delete/{id}")
    public String deleteNiveau(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            niveauService.deleteNiveau(id);
            redirectAttributes.addFlashAttribute("successMessage", "Niveau supprimé avec succès!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/niveaux";
    }
}