package com.example.student_teacher.controller;

import com.example.student_teacher.entity.Course;
import com.example.student_teacher.entity.Student;
import com.example.student_teacher.entity.Teacher;
import com.example.student_teacher.repository.CourseRepository;
import com.example.student_teacher.repository.StudentRepository;
import com.example.student_teacher.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WebController.
 * Tests login, student dashboard, and teacher dashboard views.
 */
@ExtendWith(MockitoExtension.class)
class WebControllerTest {

    @Mock
    private StudentRepository studentRepo;

    @Mock
    private TeacherRepository teacherRepo;

    @Mock
    private CourseRepository courseRepo;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private WebController webController;

    private Student testStudent;
    private Teacher testTeacher;
    private Course testCourse1;
    private Course testCourse2;

    @BeforeEach
    void setUp() {
        testTeacher = new Teacher();
        testTeacher.setName("Dr. Smith");
        testTeacher.setEmail("smith@university.edu");

        testStudent = new Student();
        testStudent.setName("John Doe");
        testStudent.setEmail("john.doe@student.edu");

        testCourse1 = new Course();
        testCourse1.setTitle("Introduction to Java");
        testCourse1.setCredit(3);
        testCourse1.setTeacher(testTeacher);

        testCourse2 = new Course();
        testCourse2.setTitle("Database Systems");
        testCourse2.setCredit(4);
        testCourse2.setTeacher(testTeacher);
    }

    /**
     * Verifies that login endpoint returns the login view.
     */
    @Test
    @DisplayName("Login returns login view")
    void login() {
        assertEquals("login", webController.login());
    }

    /**
     * Verifies that student dashboard loads with enrolled and available courses.
     */
    @Test
    @DisplayName("Student dashboard displays courses")
    void studentDashboard() {
        testStudent.getCourses().add(testCourse1);
        List<Course> allCourses = Arrays.asList(testCourse1, testCourse2);

        when(authentication.getName()).thenReturn("john.doe@student.edu");
        when(studentRepo.findByEmail("john.doe@student.edu")).thenReturn(Optional.of(testStudent));
        when(courseRepo.findAll()).thenReturn(allCourses);

        String viewName = webController.studentDashboard(authentication, model);

        assertEquals("student-dashboard", viewName);
        verify(model).addAttribute("student", testStudent);
        verify(model).addAttribute("myCourses", testStudent.getCourses());
        verify(model).addAttribute("allCourses", allCourses);
    }

    /**
     * Verifies that student dashboard handles new student with no courses.
     */
    @Test
    @DisplayName("Student dashboard handles empty enrollment")
    void studentDashboard_noEnrolledCourses() {
        when(authentication.getName()).thenReturn("john.doe@student.edu");
        when(studentRepo.findByEmail("john.doe@student.edu")).thenReturn(Optional.of(testStudent));
        when(courseRepo.findAll()).thenReturn(Arrays.asList(testCourse1, testCourse2));

        String viewName = webController.studentDashboard(authentication, model);

        assertEquals("student-dashboard", viewName);
        assertTrue(testStudent.getCourses().isEmpty());
    }

    /**
     * Verifies that teacher dashboard loads with courses and credit total.
     */
    @Test
    @DisplayName("Teacher dashboard displays courses and credits")
    void teacherDashboard() {
        List<Course> teacherCourses = Arrays.asList(testCourse1, testCourse2);

        when(authentication.getName()).thenReturn("smith@university.edu");
        when(teacherRepo.findByEmail("smith@university.edu")).thenReturn(Optional.of(testTeacher));
        when(courseRepo.findByTeacher(testTeacher)).thenReturn(teacherCourses);

        String viewName = webController.teacherDashboard(authentication, model);

        assertEquals("teacher-dashboard", viewName);
        verify(model).addAttribute("teacher", testTeacher);
        verify(model).addAttribute("myCourses", teacherCourses);
        verify(model).addAttribute("totalCredits", 7); // 3 + 4
    }

    /**
     * Verifies that teacher dashboard handles teacher with no courses.
     */
    @Test
    @DisplayName("Teacher dashboard handles empty course list")
    void teacherDashboard_noCourses() {
        when(authentication.getName()).thenReturn("smith@university.edu");
        when(teacherRepo.findByEmail("smith@university.edu")).thenReturn(Optional.of(testTeacher));
        when(courseRepo.findByTeacher(testTeacher)).thenReturn(Collections.emptyList());

        String viewName = webController.teacherDashboard(authentication, model);

        assertEquals("teacher-dashboard", viewName);
        verify(model).addAttribute("totalCredits", 0);
    }
}