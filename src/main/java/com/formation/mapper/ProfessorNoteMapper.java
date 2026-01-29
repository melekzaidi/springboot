package com.formation.mapper;

import com.formation.dto.ProfessorNoteDTO;
import com.formation.entity.ProfessorNote;
import com.formation.entity.Prof;
import com.formation.entity.Student;
import com.formation.entity.ProfCourseNiveau;
import org.springframework.stereotype.Component;

@Component
public class ProfessorNoteMapper {

    public ProfessorNoteDTO toDTO(ProfessorNote note) {
        if (note == null) {
            return null;
        }

        ProfessorNoteDTO dto = new ProfessorNoteDTO();
        dto.setId(note.getId());
        dto.setProfessorId(note.getProfessor().getId());
        dto.setStudentId(note.getStudent().getId());
        dto.setNoteContent(note.getNoteContent());
        dto.setDateCreated(note.getDateCreated());
        dto.setDateUpdated(note.getDateUpdated());
        dto.setVisibleToStudent(note.isVisibleToStudent());

        // Set display names
        dto.setProfessorName(note.getProfessor().getPrenom() + " " + note.getProfessor().getNom());
        dto.setStudentName(note.getStudent().getPrenom() + " " + note.getStudent().getNom());

        if (note.getProfCourseNiveau() != null) {
            dto.setProfCourseNiveauId(note.getProfCourseNiveau().getId());
            dto.setCourseName(note.getProfCourseNiveau().getCourseNiveau().getCourse().getNom());
            dto.setNiveauName(note.getProfCourseNiveau().getCourseNiveau().getNiveau().getNom());
        }

        return dto;
    }

    public ProfessorNote toEntity(ProfessorNoteDTO dto, Prof professor, Student student, ProfCourseNiveau profCourseNiveau) {
        if (dto == null) {
            return null;
        }

        ProfessorNote note = new ProfessorNote();
        note.setId(dto.getId());
        note.setProfessor(professor);
        note.setStudent(student);
        note.setNoteContent(dto.getNoteContent());
        note.setVisibleToStudent(dto.isVisibleToStudent());
        note.setProfCourseNiveau(profCourseNiveau);

        if (dto.getDateCreated() != null) {
            note.setDateCreated(dto.getDateCreated());
        }
        if (dto.getDateUpdated() != null) {
            note.setDateUpdated(dto.getDateUpdated());
        }

        return note;
    }

    public void updateEntityFromDTO(ProfessorNote note, ProfessorNoteDTO dto) {
        if (dto == null || note == null) {
            return;
        }

        note.setNoteContent(dto.getNoteContent());
        note.setVisibleToStudent(dto.isVisibleToStudent());
        // dateUpdated is automatically set in the entity's setNoteContent method
    }
}