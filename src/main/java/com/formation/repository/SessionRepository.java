package com.formation.repository;

import com.formation.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("SELECT s FROM Session s WHERE s.profCourseNiveau.courseNiveau.niveau.id IN " +
           "(SELECT sn.niveau.id FROM StudentNiveau sn WHERE sn.student.id = :studentId)")
    List<Session> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT s FROM Session s WHERE s.profCourseNiveau.prof.id = :profId")
    List<Session> findByProfId(@Param("profId") Long profId);
}
