package com.formation.mapper;

import com.formation.dto.AttendanceDTO;
import com.formation.entity.Attendance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AttendanceMapper {

    public AttendanceDTO toDTO(Attendance attendance) {
        if (attendance == null) return null;
        
        AttendanceDTO dto = new AttendanceDTO(
            attendance.getId(),
            attendance.getSession().getId(),
            attendance.getStudent().getId(),
            attendance.isAbsent(),
            attendance.getComment(),
            attendance.getDateCreation()
        );
        
        // Add nested information for display
        dto.setStudentNom(attendance.getStudent().getNom());
        dto.setStudentPrenom(attendance.getStudent().getPrenom());
        dto.setStudentMatricule(attendance.getStudent().getMatricule());
        
        return dto;
    }

    public List<AttendanceDTO> toDTOList(List<Attendance> attendances) {
        return attendances.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}