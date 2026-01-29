package com.formation.controller;

import com.formation.dto.ProfessorNoteDTO;
import com.formation.entity.Prof;
import com.formation.entity.Student;
import com.formation.repository.ProfRepository;
import com.formation.repository.StudentRepository;
import com.formation.service.ProfessorNoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/professor-notes")
public class ProfessorNoteController {

    @Autowired
    private ProfessorNoteService professorNoteService;

    @Autowired
    private ProfRepository profRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public String listNotes(Model model, Authentication authentication) {
        String professorEmail = authentication.getName();
        List<ProfessorNoteDTO> notes = professorNoteService.getNotesByProfessor(professorEmail);
        model.addAttribute("pageTitle", "Mes Notes sur les Étudiants");
        model.addAttribute("notes", notes);
        return "professor-notes/list";
    }

    @GetMapping("/form")
    public String showForm(Model model, Authentication authentication) {
        model.addAttribute("pageTitle", "Ajouter une Note");
        model.addAttribute("noteDTO", new ProfessorNoteDTO());
        
        // Get professor ID for the form
        String professorEmail = authentication.getName();
        Prof professor = profRepository.findByEmail(professorEmail).orElse(null);
        if (professor != null) {
            model.addAttribute("professorId", professor.getId());
        }
        
        // Get all students for the dropdown
        List<Student> students = studentRepository.findAll();
        model.addAttribute("students", students);
        
        return "professor-notes/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        String professorEmail = authentication.getName();
        try {
            ProfessorNoteDTO note = professorNoteService.getNoteById(id, professorEmail);
            model.addAttribute("pageTitle", "Modifier la Note");
            model.addAttribute("noteDTO", note);
            
            // Get professor ID for the form
            Prof professor = profRepository.findByEmail(professorEmail).orElse(null);
            if (professor != null) {
                model.addAttribute("professorId", professor.getId());
            }
            
            // Get all students for the dropdown
            List<Student> students = studentRepository.findAll();
            model.addAttribute("students", students);
            
            return "professor-notes/form";
        } catch (Exception e) {
            return "redirect:/professor-notes?error=Note non trouvée";
        }
    }

    @PostMapping("/save")
    public String saveNote(@Valid @ModelAttribute("noteDTO") ProfessorNoteDTO noteDTO,
                          BindingResult result,
                          Model model,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            // Get professor ID for the form
            String professorEmail = authentication.getName();
            Prof professor = profRepository.findByEmail(professorEmail).orElse(null);
            if (professor != null) {
                model.addAttribute("professorId", professor.getId());
            }
            
            // Get all students for the dropdown
            List<Student> students = studentRepository.findAll();
            model.addAttribute("students", students);
            
            return "professor-notes/form";
        }

        try {
            if (noteDTO.getId() != null) {
                // Update existing note
                String professorEmail = authentication.getName();
                professorNoteService.updateNote(noteDTO.getId(), noteDTO, professorEmail);
                redirectAttributes.addFlashAttribute("success", "Note mise à jour avec succès");
            } else {
                // Create new note
                professorNoteService.createNote(noteDTO);
                redirectAttributes.addFlashAttribute("success", "Note créée avec succès");
            }
            return "redirect:/professor-notes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/professor-notes/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteNote(@PathVariable Long id, 
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            String professorEmail = authentication.getName();
            professorNoteService.deleteNote(id, professorEmail);
            redirectAttributes.addFlashAttribute("success", "Note supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/professor-notes";
    }

    @GetMapping("/student/{studentId}")
    public String viewStudentNotes(@PathVariable Long studentId, 
                                 Model model, 
                                 Authentication authentication) {
        try {
            String professorEmail = authentication.getName();
            List<ProfessorNoteDTO> notes = professorNoteService.getNotesByProfessorAndStudent(professorEmail, studentId);
            
            Student student = studentRepository.findById(studentId).orElse(null);
            model.addAttribute("notes", notes);
            model.addAttribute("student", student);
            
            return "professor-notes/student-notes";
        } catch (Exception e) {
            return "redirect:/professor-notes?error=Étudiant non trouvé";
        }
    }
}