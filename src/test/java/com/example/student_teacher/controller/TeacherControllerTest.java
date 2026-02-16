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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TeacherController.
 * Tests course management and student management functionality.
 */
@ExtendWith(MockitoExtension.class)
class TeacherControllerTest {

    @Mock
    private CourseRepository courseRepo;

    @Mock
    private TeacherRepository teacherRepo;

    @Mock
    private StudentRepository studentRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Principal principal;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher testTeacher;
    private Student testStudent;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        testTeacher = new Teacher();
        testTeacher.setName("Dr. Smith");
        testTeacher.setEmail("smith@university.edu");

        testStudent = new Student();
        testStudent.setName("John Doe");
        testStudent.setEmail("john.doe@student.edu");
        testStudent.setPassword("plainPassword");

        testCourse = new Course();
        testCourse.setTitle("Introduction to Java");
        testCourse.setCredit(3);
    }

    /**
     * Verifies that a teacher can add a new course.
     * The course should be linked to the authenticated teacher.
     */
    @Test
    @DisplayName("Teacher can add a new course")
    void addCourse() {
        when(principal.getName()).thenReturn("smith@university.edu");
        when(teacherRepo.findByEmail("smith@university.edu")).thenReturn(Optional.of(testTeacher));
        when(courseRepo.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        Course result = teacherController.addCourse(testCourse, principal);

        assertNotNull(result);
        assertEquals(testTeacher, result.getTeacher());
        assertEquals("Introduction to Java", result.getTitle());
    }

    /**
     * Verifies that a teacher can retrieve all students.
     */
    @Test
    @DisplayName("Teacher can get all students")
    void getAllStudents() {
        List<Student> students = Arrays.asList(testStudent);
        when(studentRepo.findAll()).thenReturn(students);

        List<Student> result = teacherController.getAllStudents();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }

    /**
     * Verifies that a teacher can add a new student.
     * Password should be encoded before saving.
     */
    @Test
    @DisplayName("Teacher can add a new student")
    void addStudent() {
        when(studentRepo.findByEmail(testStudent.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(studentRepo.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> response = teacherController.addStudent(testStudent);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Student saved = (Student) response.getBody();
        assertEquals("encodedPassword", saved.getPassword());
    }

    /**
     * Verifies that duplicate email is rejected when adding student.
     */
    @Test
    @DisplayName("Reject duplicate email when adding student")
    void addStudent_duplicateEmail() {
        when(studentRepo.findByEmail(testStudent.getEmail())).thenReturn(Optional.of(testStudent));

        ResponseEntity<?> response = teacherController.addStudent(testStudent);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already exists", response.getBody());
    }

    /**
     * Verifies that a teacher can delete an existing student.
     */
    @Test
    @DisplayName("Teacher can delete a student")
    void deleteStudent() {
        when(studentRepo.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepo).deleteById(1L);

        ResponseEntity<?> response = teacherController.deleteStudent(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Student deleted successfully", response.getBody());
    }

    /**
     * Verifies that deleting non-existent student returns error.
     */
    @Test
    @DisplayName("Return error when deleting non-existent student")
    void deleteStudent_notFound() {
        when(studentRepo.existsById(999L)).thenReturn(false);

        ResponseEntity<?> response = teacherController.deleteStudent(999L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Student not found", response.getBody());
    }
}