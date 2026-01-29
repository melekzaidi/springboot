package com.formation.service;

import com.formation.dto.ProfessorNoteDTO;
import com.formation.entity.*;
import com.formation.mapper.ProfessorNoteMapper;
import com.formation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfessorNoteServiceImpl implements ProfessorNoteService {

    @Autowired
    private ProfessorNoteRepository professorNoteRepository;

    @Autowired
    private ProfRepository profRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProfCourseNiveauRepository profCourseNiveauRepository;

    @Autowired
    private StudentNiveauRepository studentNiveauRepository;

    @Autowired
    private ProfessorNoteMapper professorNoteMapper;

    @Override
    public ProfessorNoteDTO createNote(ProfessorNoteDTO noteDTO) {
        Prof professor = profRepository.findById(noteDTO.getProfessorId())
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));

        Student student = studentRepository.findById(noteDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        // Verify professor can access this student
        if (!canProfessorAccessStudent(professor.getEmail(), student.getId())) {
            throw new AccessDeniedException("Vous n'avez pas accès à cet étudiant");
        }

        ProfCourseNiveau profCourseNiveau = null;
        if (noteDTO.getProfCourseNiveauId() != null) {
            profCourseNiveau = profCourseNiveauRepository.findById(noteDTO.getProfCourseNiveauId())
                    .orElseThrow(() -> new RuntimeException("Assignation cours-niveau non trouvée"));
            
            // Verify this professor teaches this course
            if (!profCourseNiveau.getProf().getId().equals(professor.getId())) {
                throw new AccessDeniedException("Vous n'enseignez pas ce cours");
            }
        }

        ProfessorNote note = professorNoteMapper.toEntity(noteDTO, professor, student, profCourseNiveau);
        ProfessorNote savedNote = professorNoteRepository.save(note);
        
        return professorNoteMapper.toDTO(savedNote);
    }

    @Override
    public ProfessorNoteDTO updateNote(Long noteId, ProfessorNoteDTO noteDTO, String professorEmail) {
        Prof professor = profRepository.findByEmail(professorEmail)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));

        ProfessorNote existingNote = professorNoteRepository.findByIdAndProfessorId(noteId, professor.getId())
                .orElseThrow(() -> new AccessDeniedException("Note non trouvée ou accès refusé"));

        professorNoteMapper.updateEntityFromDTO(existingNote, noteDTO);
        ProfessorNote updatedNote = professorNoteRepository.save(existingNote);
        
        return professorNoteMapper.toDTO(updatedNote);
    }

    @Override
    public void deleteNote(Long noteId, String professorEmail) {
        Prof professor = profRepository.findByEmail(professorEmail)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));

        ProfessorNote note = professorNoteRepository.findByIdAndProfessorId(noteId, professor.getId())
                .orElseThrow(() -> new AccessDeniedException("Note non trouvée ou accès refusé"));

        professorNoteRepository.delete(note);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfessorNoteDTO getNoteById(Long noteId, String professorEmail) {
        Prof professor = profRepository.findByEmail(professorEmail)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));

        ProfessorNote note = professorNoteRepository.findByIdAndProfessorId(noteId, professor.getId())
                .orElseThrow(() -> new AccessDeniedException("Note non trouvée ou accès refusé"));

        return professorNoteMapper.toDTO(note);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorNoteDTO> getNotesByProfessor(String professorEmail) {
        Prof professor = profRepository.findByEmail(professorEmail)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));

        List<ProfessorNote> notes = professorNoteRepository.findByProfessorIdOrderByDateCreatedDesc(professor.getId());
        
        return notes.stream()
                .map(professorNoteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorNoteDTO> getNotesForStudent(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        List<ProfessorNote> notes = professorNoteRepository.findByStudentIdAndIsVisibleToStudentTrueOrderByDateCreatedDesc(student.getId());
        
        return notes.stream()
                .map(professorNoteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorNoteDTO> getNotesByProfessorAndStudent(String professorEmail, Long studentId) {
        Prof professor = profRepository.findByEmail(professorEmail)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));

        // Verify professor can access this student
        if (!canProfessorAccessStudent(professorEmail, studentId)) {
            throw new AccessDeniedException("Vous n'avez pas accès à cet étudiant");
        }

        List<ProfessorNote> notes = professorNoteRepository.findByProfessorIdAndStudentIdOrderByDateCreatedDesc(professor.getId(), studentId);
        
        return notes.stream()
                .map(professorNoteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorNoteDTO> getNotesByProfessorAndCourse(String professorEmail, Long profCourseNiveauId) {
        Prof professor = profRepository.findByEmail(professorEmail)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));

        // Verify professor teaches this course
        ProfCourseNiveau profCourseNiveau = profCourseNiveauRepository.findById(profCourseNiveauId)
                .orElseThrow(() -> new RuntimeException("Assignation cours-niveau non trouvée"));
        
        if (!profCourseNiveau.getProf().getId().equals(professor.getId())) {
            throw new AccessDeniedException("Vous n'enseignez pas ce cours");
        }

        List<ProfessorNote> notes = professorNoteRepository.findByProfessorIdAndProfCourseNiveauIdOrderByDateCreatedDesc(professor.getId(), profCourseNiveauId);
        
        return notes.stream()
                .map(professorNoteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorNoteDTO> getNotesForStudentInCourse(String studentEmail, Long profCourseNiveauId) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        List<ProfessorNote> notes = professorNoteRepository.findByStudentIdAndProfCourseNiveauIdAndIsVisibleToStudentTrueOrderByDateCreatedDesc(student.getId(), profCourseNiveauId);
        
        return notes.stream()
                .map(professorNoteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canProfessorAccessStudent(String professorEmail, Long studentId) {
        Prof professor = profRepository.findByEmail(professorEmail)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        // Check if professor teaches any course that the student is enrolled in
        List<StudentNiveau> studentNiveaux = studentNiveauRepository.findByStudent(student);
        
        for (StudentNiveau studentNiveau : studentNiveaux) {
            List<ProfCourseNiveau> profCourseNiveaux = profCourseNiveauRepository.findByProfIdAndCourseNiveauNiveauId(
                    professor.getId(), studentNiveau.getNiveau().getId());
            
            if (!profCourseNiveaux.isEmpty()) {
                return true;
            }
        }
        
        return false;
    }
}