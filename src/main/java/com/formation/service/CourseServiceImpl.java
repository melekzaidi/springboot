package com.formation.service;

import com.formation.entity.Course;
import com.formation.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course updateCourse(Long id, Course courseDetails) {
        Course course = getCourseById(id);
        course.setCode(courseDetails.getCode());
        course.setNom(courseDetails.getNom());
        course.setDescription(courseDetails.getDescription());
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        courseRepository.delete(course);
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé avec l'ID: " + id));
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAllByOrderByNomAsc();
    }

    @Override
    public boolean isCodeUnique(String code) {
        return !courseRepository.existsByCode(code);
    }
}