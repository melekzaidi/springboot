package com.formation.controller;

import com.formation.entity.Attendance;
import com.formation.entity.Prof;
import com.formation.entity.Session;
import com.formation.entity.Student;
import com.formation.entity.StudentNiveau;
import com.formation.service.CalendarService;
import com.formation.repository.StudentNiveauRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private StudentNiveauRepository studentNiveauRepository;

    @Autowired
    private com.formation.repository.StudentRepository studentRepository;

    @Autowired
    private com.formation.repository.ProfRepository profRepository;

    @GetMapping("/calendar")
    public String viewCalendar(@RequestParam(name = "date", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date, 
                               Authentication authentication, 
                               Model model) {
        if (authentication == null) return "redirect:/login";
        
        if (date == null) {
            date = java.time.LocalDate.now();
        }

        // Calculate start and end of week (Monday to Sunday)
        java.time.LocalDate startOfWeek = date.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        java.time.LocalDate endOfWeek = date.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

        String email = authentication.getName();
        String role = authentication.getAuthorities().stream().findFirst().get().getAuthority();
        
        List<Session> allSessions = new java.util.ArrayList<>();

        if ("ROLE_STUDENT".equals(role)) {
            Student student = studentRepository.findByEmail(email).orElse(null);
            if (student != null) {
                allSessions = calendarService.getSessionsForStudent(student.getId());
            }
        } else if ("ROLE_PROF".equals(role)) {
            Prof prof = profRepository.findByEmail(email).orElse(null);
            if (prof != null) {
                allSessions = calendarService.getSessionsForProf(prof.getId());
            }
        } else if ("ROLE_ADMIN".equals(role)) {
            // Admin sees all sessions
            allSessions = calendarService.getAllSessions();
        }
        
        // Filter sessions for the current week
        List<Session> weeklySessions = allSessions.stream()
                .filter(s -> {
                    java.time.LocalDate sessionDate = s.getStartDateTime().toLocalDate();
                    return !sessionDate.isBefore(startOfWeek) && !sessionDate.isAfter(endOfWeek);
                })
                .sorted(java.util.Comparator.comparing(Session::getStartDateTime))
                .collect(Collectors.toList());

        model.addAttribute("sessions", weeklySessions);
        model.addAttribute("currentDate", date);
        model.addAttribute("weekStart", startOfWeek);
        model.addAttribute("weekEnd", endOfWeek);
        
        // Previous and Next week dates for navigation
        model.addAttribute("prevWeek", date.minusWeeks(1));
        model.addAttribute("nextWeek", date.plusWeeks(1));

        return "calendar";
    }

    @GetMapping("/session/{id}")
    public String viewSession(@PathVariable("id") Long sessionId, Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";

        String role = authentication.getAuthorities().stream().findFirst().get().getAuthority();
        
        Session sessionEntity = calendarService.getSessionById(sessionId);
        model.addAttribute("courseSession", sessionEntity);
        
        // Load attendance
        List<Attendance> attendances = calendarService.getAttendanceForSession(sessionId);
        model.addAttribute("attendances", attendances);

        if ("ROLE_PROF".equals(role)) {
             // For profs, we need to show all students in the class
            List<Student> allStudents = studentNiveauRepository.findByNiveauId(sessionEntity.getProfCourseNiveau().getCourseNiveau().getNiveau().getId())
                    .stream().map(StudentNiveau::getStudent).collect(Collectors.toList());
            
            model.addAttribute("allStudents", allStudents);
        }

        return "session-details";
    }

    @PostMapping("/session/{id}/attendance")
    public String updateAttendance(@PathVariable("id") Long sessionId,
                                   @RequestParam(name = "absentStudentIds", required = false) List<Long> absentStudentIds,
                                   Authentication authentication) {
        if (authentication == null) return "redirect:/login";
        
        String role = authentication.getAuthorities().stream().findFirst().get().getAuthority();
        if (!"ROLE_PROF".equals(role)) {
             return "redirect:/login";
        }
        
        Session sessionEntity = calendarService.getSessionById(sessionId);
        List<Student> allStudents = studentNiveauRepository.findByNiveauId(sessionEntity.getProfCourseNiveau().getCourseNiveau().getNiveau().getId())
                    .stream().map(StudentNiveau::getStudent).collect(Collectors.toList());

        for (Student student : allStudents) {
            boolean isAbsent = absentStudentIds != null && absentStudentIds.contains(student.getId());
            calendarService.markAttendance(sessionId, student.getId(), isAbsent, "");
        }

        return "redirect:/session/" + sessionId;
    }
}
