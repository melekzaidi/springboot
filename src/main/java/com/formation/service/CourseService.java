package com.formation.service;

import com.formation.entity.Course;
import java.util.List;

public interface CourseService {
    Course saveCourse(Course course);
    Course updateCourse(Long id, Course course);
    void deleteCourse(Long id);
    Course getCourseById(Long id);
    List<Course> getAllCourses();
    boolean isCodeUnique(String code);
}