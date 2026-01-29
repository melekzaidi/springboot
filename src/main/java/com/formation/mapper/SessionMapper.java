package com.formation.mapper;

import com.formation.dto.SessionDTO;
import com.formation.entity.Session;
import com.formation.entity.ProfCourseNiveau;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SessionMapper {

    public SessionDTO toDTO(Session session) {
        if (session == null) return null;
        
        SessionDTO dto = new SessionDTO(
            session.getId(),
            session.getStartDateTime(),
            session.getEndDateTime(),
            session.getProfCourseNiveau().getId(),
            session.getDescription(),
            session.getDateCreation()
        );
        
        // Add nested information for display
        ProfCourseNiveau pcn = session.getProfCourseNiveau();
        if (pcn != null) {
            dto.setProfNom(pcn.getProf().getNomComplet());
            dto.setCourseName(pcn.getCourseNiveau().getCourse().getNom());
            dto.setNiveauName(pcn.getCourseNiveau().getNiveau().getNom());
        }
        
        return dto;
    }

    public Session toEntity(SessionDTO dto, ProfCourseNiveau profCourseNiveau) {
        if (dto == null) return null;
        
        Session session = new Session();
        session.setId(dto.getId());
        session.setStartDateTime(dto.getStartDateTime());
        session.setEndDateTime(dto.getEndDateTime());
        session.setProfCourseNiveau(profCourseNiveau);
        session.setDescription(dto.getDescription());
        session.setDateCreation(dto.getDateCreation());
        
        return session;
    }

    public List<SessionDTO> toDTOList(List<Session> sessions) {
        return sessions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromDTO(SessionDTO dto, Session session, ProfCourseNiveau profCourseNiveau) {
        if (dto == null || session == null) return;
        
        session.setStartDateTime(dto.getStartDateTime());
        session.setEndDateTime(dto.getEndDateTime());
        session.setProfCourseNiveau(profCourseNiveau);
        session.setDescription(dto.getDescription());
    }
}