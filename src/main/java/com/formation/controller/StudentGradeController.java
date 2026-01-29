package com.formation.controller;

import com.formation.dto.StudentGradesSummaryDTO;
import com.formation.entity.Grade;
import com.formation.entity.Student;
import com.formation.mapper.GradeMapper;
import com.formation.service.GradeService;
import com.formation.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student-grades")
@PreAuthorize("hasRole('STUDENT')")
public class StudentGradeController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private GradeMapper gradeMapper;

    @GetMapping
    public String viewMyGrades(Model model, Authentication authentication) {
        try {
            String email = authentication.getName();
            Student student = studentService.getStudentByEmail(email);
            
            // Get all grades for this student
            List<Grade> allGrades = gradeService.getGradesByStudentId(student.getId());
            
            // Group by prof-course-niveau and create summaries
            Map<Long, List<Grade>> gradesByProfCourseNiveau = allGrades.stream()
                .collect(Collectors.groupingBy(grade -> grade.getProfCourseNiveau().getId()));
            
            List<StudentGradesSummaryDTO> summaries = new ArrayList<>();
            
            for (Map.Entry<Long, List<Grade>> entry : gradesByProfCourseNiveau.entrySet()) {
                List<Grade> grades = entry.getValue();
                
                if (!grades.isEmpty()) {
                    Grade firstGrade = grades.get(0);
                    var pcn = firstGrade.getProfCourseNiveau();
                    
                    StudentGradesSummaryDTO summary = new StudentGradesSummaryDTO(
                        student.getId(),
                        student.getNom(),
                        student.getPrenom(),
                        student.getMatricule(),
                        pcn.getId(),
                        pcn.getCourseNiveau().getCourse().getNom(),
                        pcn.getCourseNiveau().getNiveau().getNom(),
                        pcn.getProf().getNom(),
                        pcn.getProf().getPrenom()
                    );
                    
                    // Set DS and Examen notes
                    for (Grade grade : grades) {
                        if (grade.getGradeType() == Grade.GradeType.DS) {
                            summary.setNoteDS(grade.getNote());
                        } else if (grade.getGradeType() == Grade.GradeType.EXAMEN) {
                            summary.setNoteExamen(grade.getNote());
                        }
                    }
                    
                    // Calculate moyenne
                    summary.calculateMoyenne();
                    summaries.add(summary);
                }
            }
            
            model.addAttribute("student", student);
            model.addAttribute("gradesSummaries", summaries);
            model.addAttribute("allGrades", gradeMapper.toDTOList(allGrades));
            
            return "student-grades/my-grades";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération de vos notes: " + e.getMessage());
            return "error";
        }
    }
}