package com.formation.controller;

import com.formation.dto.ProfessorNoteDTO;
import com.formation.service.ProfessorNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/student-notes")
public class StudentNoteController {

    @Autowired
    private ProfessorNoteService professorNoteService;

    @GetMapping
    public String viewMyRemarks(Model model, Authentication authentication) {
        String studentEmail = authentication.getName();
        List<ProfessorNoteDTO> notes = professorNoteService.getNotesForStudent(studentEmail);
        model.addAttribute("pageTitle", "Mes Remarques");
        model.addAttribute("notes", notes);
        return "student-notes/my-notes";
    }
}