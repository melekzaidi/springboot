package com.formation.service;

import com.formation.dto.ProfessorNoteDTO;
import java.util.List;

public interface ProfessorNoteService {
    
    ProfessorNoteDTO createNote(ProfessorNoteDTO noteDTO);
    
    ProfessorNoteDTO updateNote(Long noteId, ProfessorNoteDTO noteDTO, String professorEmail);
    
    void deleteNote(Long noteId, String professorEmail);
    
    ProfessorNoteDTO getNoteById(Long noteId, String professorEmail);
    
    List<ProfessorNoteDTO> getNotesByProfessor(String professorEmail);
    
    List<ProfessorNoteDTO> getNotesForStudent(String studentEmail);
    
    List<ProfessorNoteDTO> getNotesByProfessorAndStudent(String professorEmail, Long studentId);
    
    List<ProfessorNoteDTO> getNotesByProfessorAndCourse(String professorEmail, Long profCourseNiveauId);
    
    List<ProfessorNoteDTO> getNotesForStudentInCourse(String studentEmail, Long profCourseNiveauId);
    
    boolean canProfessorAccessStudent(String professorEmail, Long studentId);
}