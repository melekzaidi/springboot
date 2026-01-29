package com.formation.controller;

import com.formation.dto.CourseNiveauStatsDTO;
import com.formation.entity.Prof;
import com.formation.entity.Student;
import com.formation.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ProfService profService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private NiveauService niveauService;

    @Autowired
    private CourseNiveauService courseNiveauService;

    @Autowired
    private ProfCourseNiveauService profCourseNiveauService;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/")
    public String home(Authentication authentication) {
        // Redirect users based on their role after login
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if ("ROLE_ADMIN".equals(role)) {
            return "redirect:/dashboard";
        } else {
            return "redirect:/calendar";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String userRole = authentication.getAuthorities().iterator().next().getAuthority();
        String userEmail = authentication.getName();
        
        model.addAttribute("pageTitle", "Tableau de Bord - Centre de Formation");
        model.addAttribute("userRole", userRole.replace("ROLE_", ""));
        
        try {
            if ("ROLE_ADMIN".equals(userRole)) {
                // Admin dashboard
                model.addAttribute("totalStudents", studentService.countStudents());
                model.addAttribute("totalProfs", profService.getAllProfs().size());
                model.addAttribute("totalCourses", courseService.getAllCourses().size());
                model.addAttribute("totalNiveaux", niveauService.getAllNiveaux().size());
                model.addAttribute("totalCourseNiveaux", courseNiveauService.getAllCourseNiveaux().size());
                model.addAttribute("totalAssignments", profCourseNiveauService.getAllProfCourseNiveaux().size());
                model.addAttribute("totalGrades", gradeService.getAllGrades().size());
                
                // Add course-niveau statistics for BI chart
                try {
                    CourseNiveauStatsDTO courseNiveauStats = dashboardService.getCourseNiveauStats();
                    System.out.println("DEBUG - Course Niveau Stats: " + courseNiveauStats.getTotalStudentEnrollments() + " enrollments, " + courseNiveauStats.getTotalCourseNiveaux() + " niveaux");
                    if (courseNiveauStats.getDistributions() != null) {
                        System.out.println("DEBUG - Distributions count: " + courseNiveauStats.getDistributions().size());
                        courseNiveauStats.getDistributions().forEach(dist -> 
                            System.out.println("DEBUG - " + dist.getNiveauName() + ": " + dist.getStudentCount() + " students")
                        );
                    }
                    model.addAttribute("courseNiveauStats", courseNiveauStats);
                } catch (Exception e) {
                    System.err.println("ERROR in dashboard service: " + e.getMessage());
                    e.printStackTrace();
                    // Fallback to default values if service fails
                    CourseNiveauStatsDTO defaultStats = new CourseNiveauStatsDTO();
                    defaultStats.setTotalCourseNiveaux(0L);
                    defaultStats.setTotalStudentEnrollments(0L);
                    defaultStats.setDistributions(new java.util.ArrayList<>());
                    defaultStats.setMostPopularCourseNiveau("Aucun");
                    defaultStats.setLeastPopularCourseNiveau("Aucun");
                    model.addAttribute("courseNiveauStats", defaultStats);
                }
                
            } else if ("ROLE_PROF".equals(userRole)) {
                // Professor dashboard
                Prof prof = profService.getProfByEmail(userEmail);
                model.addAttribute("prof", prof);
                model.addAttribute("myAssignments", profCourseNiveauService.getProfCourseNiveauxByProfId(prof.getId()));
                model.addAttribute("myGradesCount", gradeService.getGradesByProfId(prof.getId()).size());
                
            } else if ("ROLE_STUDENT".equals(userRole)) {
                // Student dashboard
                Student student = studentService.getStudentByEmail(userEmail);
                model.addAttribute("student", student);
                model.addAttribute("myGrades", gradeService.getGradesByStudentId(student.getId()));
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement des données: " + e.getMessage());
        }
        
        return "home";
    }
}