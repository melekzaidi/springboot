package com.formation.service;

import com.formation.dto.StudentGradesSummaryDTO;
import com.formation.entity.Grade;
import com.formation.entity.ProfCourseNiveau;
import com.formation.entity.Student;
import com.formation.entity.StudentNiveau;
import com.formation.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class GradeServiceImpl implements GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ProfCourseNiveauService profCourseNiveauService;

    @Autowired
    private StudentNiveauService studentNiveauService;

    @Override
    public Grade saveGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    @Override
    public void deleteGrade(Long id) {
        Grade grade = getGradeById(id);
        gradeRepository.delete(grade);
    }

    @Override
    public Grade getGradeById(Long id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note non trouvée avec l'ID: " + id));
    }

    @Override
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    @Override
    public List<Grade> getGradesByStudent(Student student) {
        return gradeRepository.findByStudent(student);
    }

    @Override
    public List<Grade> getGradesByProfCourseNiveau(ProfCourseNiveau profCourseNiveau) {
        return gradeRepository.findByProfCourseNiveau(profCourseNiveau);
    }

    @Override
    public List<Grade> getGradesByStudentAndProfCourseNiveau(Student student, ProfCourseNiveau profCourseNiveau) {
        return gradeRepository.findByStudentAndProfCourseNiveau(student, profCourseNiveau);
    }

    @Override
    public List<Grade> getGradesByProfId(Long profId) {
        return gradeRepository.findByProfId(profId);
    }

    @Override
    public List<Grade> getGradesByStudentId(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    @Override
    public List<Grade> getGradesByProfCourseNiveauId(Long profCourseNiveauId) {
        return gradeRepository.findByProfCourseNiveauId(profCourseNiveauId);
    }

    @Override
    public List<Grade> getGradesByStudentIdAndProfCourseNiveauId(Long studentId, Long profCourseNiveauId) {
        return gradeRepository.findByStudentIdAndProfCourseNiveauId(studentId, profCourseNiveauId);
    }

    @Override
    public boolean existsGradeForStudentAndProfCourseNiveauAndType(
            Student student, ProfCourseNiveau profCourseNiveau, Grade.GradeType gradeType) {
        return gradeRepository.existsByStudentAndProfCourseNiveauAndGradeType(student, profCourseNiveau, gradeType);
    }

    @Override
    public Grade findGradeByStudentAndProfCourseNiveauAndType(
            Student student, ProfCourseNiveau profCourseNiveau, Grade.GradeType gradeType) {
        return gradeRepository.findByStudentAndProfCourseNiveauAndGradeType(student, profCourseNiveau, gradeType)
                .orElse(null);
    }

    @Override
    public List<StudentGradesSummaryDTO> getStudentGradesSummaryByProfCourseNiveau(Long profCourseNiveauId) {
        ProfCourseNiveau profCourseNiveau = profCourseNiveauService.getProfCourseNiveauById(profCourseNiveauId);
        
        // Get all students enrolled in this course-niveau
        List<StudentNiveau> studentNiveaux = studentNiveauService.getStudentNiveauxByCourseNiveauId(
            profCourseNiveau.getCourseNiveau().getId());
        
        List<StudentGradesSummaryDTO> summaries = new ArrayList<>();
        
        for (StudentNiveau studentNiveau : studentNiveaux) {
            Student student = studentNiveau.getStudent();
            
            StudentGradesSummaryDTO summary = new StudentGradesSummaryDTO(
                student.getId(),
                student.getNom(),
                student.getPrenom(),
                student.getMatricule(),
                profCourseNiveauId,
                profCourseNiveau.getCourseNiveau().getCourse().getNom(),
                profCourseNiveau.getCourseNiveau().getNiveau().getNom(),
                profCourseNiveau.getProf().getNom(),
                profCourseNiveau.getProf().getPrenom()
            );
            
            // Get grades for this student in this prof-course-niveau
            List<Grade> grades = getGradesByStudentAndProfCourseNiveau(student, profCourseNiveau);
            
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
        
        return summaries;
    }

    @Override
    public Double calculateMoyenneForStudent(Long studentId, Long profCourseNiveauId) {
        List<Grade> grades = getGradesByStudentIdAndProfCourseNiveauId(studentId, profCourseNiveauId);
        
        Double noteDS = null;
        Double noteExamen = null;
        
        for (Grade grade : grades) {
            if (grade.getGradeType() == Grade.GradeType.DS) {
                noteDS = grade.getNote();
            } else if (grade.getGradeType() == Grade.GradeType.EXAMEN) {
                noteExamen = grade.getNote();
            }
        }
        
        if (noteDS != null && noteExamen != null) {
            return (0.3 * noteDS) + (0.7 * noteExamen);
        }
        
        return null;
    }
}