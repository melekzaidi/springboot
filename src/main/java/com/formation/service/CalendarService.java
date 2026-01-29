package com.formation.service;

import com.formation.entity.*;
import com.formation.repository.AttendanceRepository;
import com.formation.repository.SessionRepository;
import com.formation.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CalendarService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    public List<Session> getSessionsForStudent(Long studentId) {
        return sessionRepository.findByStudentId(studentId);
    }

    public List<Session> getSessionsForProf(Long profId) {
        return sessionRepository.findByProfId(profId);
    }

    public Session getSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session non trouvée"));
    }

    public List<Attendance> getAttendanceForSession(Long sessionId) {
        return attendanceRepository.findBySessionId(sessionId);
    }

    @Transactional
    public void markAttendance(Long sessionId, Long studentId, boolean isAbsent, String comment) {
        Session session = getSessionById(sessionId);
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        Optional<Attendance> existingAttendance = attendanceRepository.findBySessionIdAndStudentId(sessionId, studentId);

        Attendance attendance;
        if (existingAttendance.isPresent()) {
            attendance = existingAttendance.get();
            attendance.setAbsent(isAbsent);
            attendance.setComment(comment);
        } else {
            attendance = new Attendance(session, student, isAbsent, comment);
        }
        attendanceRepository.save(attendance);
    }

    @Transactional
    public void saveSession(Session session) {
        sessionRepository.save(session);
    }

    @Transactional
    public void deleteSession(Long sessionId) {
        // First delete associated attendance records to avoid constraint violations
        attendanceRepository.deleteBySessionId(sessionId);
        sessionRepository.deleteById(sessionId);
    }
}
