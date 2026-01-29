package com.formation.controller.api;

import com.formation.dto.ApiResponseDTO;
import com.formation.dto.AttendanceDTO;
import com.formation.entity.Attendance;
import com.formation.mapper.AttendanceMapper;
import com.formation.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendances")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AttendanceRestController {

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AttendanceMapper attendanceMapper;

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<List<AttendanceDTO>>> getAttendanceBySession(@PathVariable Long sessionId) {
        try {
            List<Attendance> attendances = calendarService.getAttendanceForSession(sessionId);
            List<AttendanceDTO> attendanceDTOs = attendanceMapper.toDTOList(attendances);
            return ResponseEntity.ok(ApiResponseDTO.success(attendanceDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des présences: " + e.getMessage()));
        }
    }

    @PostMapping("/session/{sessionId}/mark")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<String>> markAttendance(
            @PathVariable Long sessionId,
            @RequestBody Map<String, Object> attendanceData) {
        try {
            Long studentId = Long.valueOf(attendanceData.get("studentId").toString());
            Boolean isAbsent = Boolean.valueOf(attendanceData.get("isAbsent").toString());
            String comment = attendanceData.get("comment") != null ? attendanceData.get("comment").toString() : "";

            calendarService.markAttendance(sessionId, studentId, isAbsent, comment);
            
            return ResponseEntity.ok(ApiResponseDTO.success("Présence enregistrée avec succès", "OK"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de l'enregistrement de la présence: " + e.getMessage()));
        }
    }

    @PostMapping("/session/{sessionId}/mark-multiple")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROF')")
    public ResponseEntity<ApiResponseDTO<String>> markMultipleAttendance(
            @PathVariable Long sessionId,
            @RequestBody Map<String, List<Long>> attendanceData) {
        try {
            List<Long> absentStudentIds = attendanceData.get("absentStudentIds");
            List<Long> allStudentIds = attendanceData.get("allStudentIds");

            // Mark all students as present first, then mark absent ones
            for (Long studentId : allStudentIds) {
                boolean isAbsent = absentStudentIds != null && absentStudentIds.contains(studentId);
                calendarService.markAttendance(sessionId, studentId, isAbsent, "");
            }
            
            return ResponseEntity.ok(ApiResponseDTO.success("Présences enregistrées avec succès", "OK"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de l'enregistrement des présences: " + e.getMessage()));
        }
    }
}