package com.formation.controller.api;

import com.formation.dto.ApiResponseDTO;
import com.formation.dto.StudentDTO;
import com.formation.entity.Student;
import com.formation.mapper.StudentMapper;
import com.formation.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class StudentRestController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentMapper studentMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<StudentDTO>>> getAllStudents(
            @RequestParam(value = "keyword", required = false) String keyword) {
        try {
            List<Student> students;
            if (keyword != null && !keyword.trim().isEmpty()) {
                students = studentService.searchStudents(keyword);
            } else {
                students = studentService.getAllStudents();
            }
            
            List<StudentDTO> studentDTOs = studentMapper.toDTOList(students);
            return ResponseEntity.ok(ApiResponseDTO.success(studentDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la récupération des étudiants: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<StudentDTO>> getStudentById(@PathVariable Long id) {
        try {
            Student student = studentService.getStudentById(id);
            StudentDTO studentDTO = studentMapper.toDTO(student);
            return ResponseEntity.ok(ApiResponseDTO.success(studentDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<StudentDTO>> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        try {
            // Check uniqueness
            if (!studentService.isMatriculeUnique(studentDTO.getMatricule())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Ce matricule existe déjà"));
            }
            
            if (!studentService.isEmailUnique(studentDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Cet email existe déjà"));
            }

            Student student = studentMapper.toEntity(studentDTO);
            Student savedStudent = studentService.saveStudent(student);
            StudentDTO savedStudentDTO = studentMapper.toDTO(savedStudent);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Étudiant créé avec succès", savedStudentDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la création de l'étudiant: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<StudentDTO>> updateStudent(
            @PathVariable Long id, 
            @Valid @RequestBody StudentDTO studentDTO) {
        try {
            Student existingStudent = studentService.getStudentById(id);
            
            // Check uniqueness for changed fields
            if (!existingStudent.getMatricule().equals(studentDTO.getMatricule()) &&
                !studentService.isMatriculeUnique(studentDTO.getMatricule())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Ce matricule existe déjà"));
            }
            
            if (!existingStudent.getEmail().equals(studentDTO.getEmail()) &&
                !studentService.isEmailUnique(studentDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Cet email existe déjà"));
            }

            Student updatedStudent = studentService.updateStudent(id, studentMapper.toEntity(studentDTO));
            StudentDTO updatedStudentDTO = studentMapper.toDTO(updatedStudent);
            
            return ResponseEntity.ok(ApiResponseDTO.success("Étudiant modifié avec succès", updatedStudentDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la modification de l'étudiant: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<String>> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok(ApiResponseDTO.success("Étudiant supprimé avec succès", "OK"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors de la suppression de l'étudiant: " + e.getMessage()));
        }
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Long>> getStudentCount() {
        try {
            Long count = studentService.countStudents();
            return ResponseEntity.ok(ApiResponseDTO.success(count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Erreur lors du comptage des étudiants: " + e.getMessage()));
        }
    }
}