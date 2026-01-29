package com.formation.repository;

import com.formation.entity.ProfessorNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorNoteRepository extends JpaRepository<ProfessorNote, Long> {

    // Find all notes by professor
    List<ProfessorNote> findByProfessorIdOrderByDateCreatedDesc(Long professorId);

    // Find all notes for a student (visible to student)
    List<ProfessorNote> findByStudentIdAndIsVisibleToStudentTrueOrderByDateCreatedDesc(Long studentId);

    // Find all notes for a student by a specific professor
    List<ProfessorNote> findByProfessorIdAndStudentIdOrderByDateCreatedDesc(Long professorId, Long studentId);

    // Find notes by professor and course niveau
    List<ProfessorNote> findByProfessorIdAndProfCourseNiveauIdOrderByDateCreatedDesc(Long professorId, Long profCourseNiveauId);

    // Find notes for a student in a specific course
    List<ProfessorNote> findByStudentIdAndProfCourseNiveauIdAndIsVisibleToStudentTrueOrderByDateCreatedDesc(Long studentId, Long profCourseNiveauId);

    // Check if professor can access this note (security check)
    @Query("SELECT n FROM ProfessorNote n WHERE n.id = :noteId AND n.professor.id = :professorId")
    Optional<ProfessorNote> findByIdAndProfessorId(@Param("noteId") Long noteId, @Param("professorId") Long professorId);

    // Get all notes for students taught by a professor
    @Query("SELECT DISTINCT n FROM ProfessorNote n " +
           "JOIN ProfCourseNiveau pcn ON n.profCourseNiveau.id = pcn.id " +
           "JOIN StudentNiveau sn ON sn.niveau.id = pcn.courseNiveau.niveau.id " +
           "WHERE pcn.prof.id = :professorId AND sn.student.id = n.student.id " +
           "ORDER BY n.dateCreated DESC")
    List<ProfessorNote> findNotesByProfessorForTheirStudents(@Param("professorId") Long professorId);

    // Count notes by professor
    long countByProfessorId(Long professorId);

    // Count notes for student
    long countByStudentIdAndIsVisibleToStudentTrue(Long studentId);
}