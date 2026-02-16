package com.example.student_teacher.controller;

import com.example.student_teacher.entity.Course;
import com.example.student_teacher.entity.Student;
import com.example.student_teacher.entity.Teacher;
import com.example.student_teacher.repository.CourseRepository;
import com.example.student_teacher.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentController.
 * Tests course enrollment and removal functionality.
 */
@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentRepository studentRepo;

    @Mock
    private CourseRepository courseRepo;

    @Mock
    private Principal principal;

    @InjectMocks
    private StudentController studentController;

    private Student testStudent;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        testCourse = new Course();
        testCourse.setTitle("Introduction to Java");
        testCourse.setCredit(3);

        testStudent = new Student();
        testStudent.setName("John Doe");
        testStudent.setEmail("john.doe@student.edu");
    }

    /**
     * Verifies that a student can enroll in a course.
     * The course should be added to the student's course set.
     */
    @Test
    @DisplayName("Student can enroll in a course")
    void takeCourse() {
        when(principal.getName()).thenReturn("john.doe@student.edu");
        when(studentRepo.findByEmail("john.doe@student.edu")).thenReturn(Optional.of(testStudent));
        when(courseRepo.findById(1L)).thenReturn(Optional.of(testCourse));
        when(studentRepo.save(any(Student.class))).thenReturn(testStudent);

        String result = studentController.takeCourse(1L, principal);

        assertEquals("Course taken", result);
        assertTrue(testStudent.getCourses().contains(testCourse));
    }

    /**
     * Verifies that a student can drop a course.
     * The course should be removed from the student's course set.
     */
    @Test
    @DisplayName("Student can drop a course")
    void removeCourse() {
        Course spyCourse = spy(testCourse);
        when(spyCourse.getId()).thenReturn(1L);
        testStudent.getCourses().add(spyCourse);

        when(principal.getName()).thenReturn("john.doe@student.edu");
        when(studentRepo.findByEmail("john.doe@student.edu")).thenReturn(Optional.of(testStudent));
        when(studentRepo.save(any(Student.class))).thenReturn(testStudent);

        String result = studentController.removeCourse(1L, principal);

        assertEquals("Course removed", result);
        assertEquals(0, testStudent.getCourses().size());
    }
}