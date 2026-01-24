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

    @GetMapping("/calendar")
    public String viewCalendar(HttpSession session, Model model) {
        String userType = (String) session.getAttribute("userType");
        if (userType == null) {
            return "redirect:/login";
        }

        if ("student".equals(userType)) {
            Student student = (Student) session.getAttribute("user");
            List<Session> sessions = calendarService.getSessionsForStudent(student.getId());
            model.addAttribute("sessions", sessions);
            model.addAttribute("userRole", "student");
        } else if ("prof".equals(userType)) {
            Prof prof = (Prof) session.getAttribute("user");
            List<Session> sessions = calendarService.getSessionsForProf(prof.getId());
            model.addAttribute("sessions", sessions);
            model.addAttribute("userRole", "prof");
        }

        return "calendar";
    }

    @GetMapping("/session/{id}")
    public String viewSession(@PathVariable("id") Long sessionId, HttpSession session, Model model) {
        String userType = (String) session.getAttribute("userType");
        if (userType == null) {
            return "redirect:/login";
        }

        Session sessionEntity = calendarService.getSessionById(sessionId);
        model.addAttribute("courseSession", sessionEntity);
        
        // Load attendance
        List<Attendance> attendances = calendarService.getAttendanceForSession(sessionId);
        model.addAttribute("attendances", attendances);

        if ("prof".equals(userType)) {
             // For profs, we need to show all students in the class, not just those with existing attendance records
            List<Student> allStudents = studentNiveauRepository.findByNiveauId(sessionEntity.getProfCourseNiveau().getCourseNiveau().getNiveau().getId())
                    .stream().map(StudentNiveau::getStudent).collect(Collectors.toList());
            
            model.addAttribute("allStudents", allStudents);
            model.addAttribute("userRole", "prof");
        } else {
             model.addAttribute("userRole", "student");
        }

        return "session-details";
    }

    @PostMapping("/session/{id}/attendance")
    public String updateAttendance(@PathVariable("id") Long sessionId,
                                   @RequestParam(name = "absentStudentIds", required = false) List<Long> absentStudentIds,
                                   HttpSession session) {
        String userType = (String) session.getAttribute("userType");
        if (!"prof".equals(userType)) {
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
