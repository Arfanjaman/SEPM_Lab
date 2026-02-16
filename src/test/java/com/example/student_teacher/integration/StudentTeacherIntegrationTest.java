package com.example.student_teacher.integration;

import com.example.student_teacher.entity.Course;
import com.example.student_teacher.entity.Student;
import com.example.student_teacher.entity.Teacher;
import com.example.student_teacher.repository.CourseRepository;
import com.example.student_teacher.repository.StudentRepository;
import com.example.student_teacher.repository.TeacherRepository;
import com.example.student_teacher.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Student-Teacher application.
 * 
 * Uses H2 in-memory database and full Spring context.
 * Tests the full flow from service to repository to database.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StudentTeacherIntegrationTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Student testStudent;
    private Teacher testTeacher;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Clear existing data
        courseRepository.deleteAll();
        studentRepository.deleteAll();
        teacherRepository.deleteAll();

        // Create test teacher
        testTeacher = new Teacher();
        testTeacher.setName("Dr. Smith");
        testTeacher.setEmail("smith@university.edu");
        testTeacher.setPassword(passwordEncoder.encode("password123"));
        testTeacher = teacherRepository.save(testTeacher);

        // Create test student
        testStudent = new Student();
        testStudent.setName("John Doe");
        testStudent.setEmail("john@student.edu");
        testStudent.setPassword(passwordEncoder.encode("password123"));
        testStudent = studentRepository.save(testStudent);

        // Create test course
        testCourse = new Course();
        testCourse.setTitle("Java Programming");
        testCourse.setCredit(3);
        testCourse.setTeacher(testTeacher);
        testCourse = courseRepository.save(testCourse);
    }

    /**
     * Tests that student can be saved and retrieved from database.
     */
    @Test
    @DisplayName("Student is persisted in database")
    void studentPersistence() {
        assertTrue(studentRepository.findByEmail("john@student.edu").isPresent());
        assertEquals("John Doe", studentRepository.findByEmail("john@student.edu").get().getName());
    }

    /**
     * Tests that teacher can be saved and retrieved from database.
     */
    @Test
    @DisplayName("Teacher is persisted in database")
    void teacherPersistence() {
        assertTrue(teacherRepository.findByEmail("smith@university.edu").isPresent());
        assertEquals("Dr. Smith", teacherRepository.findByEmail("smith@university.edu").get().getName());
    }

    /**
     * Tests that course is linked to teacher.
     */
    @Test
    @DisplayName("Course is linked to teacher")
    void coursePersistence() {
        List<Course> courses = courseRepository.findByTeacher(testTeacher);
        assertEquals(1, courses.size());
        assertEquals("Java Programming", courses.get(0).getTitle());
    }

    /**
     * Tests student authentication through UserDetailsService.
     */
    @Test
    @DisplayName("Student can authenticate")
    void studentAuthentication() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("john@student.edu");
        
        assertEquals("john@student.edu", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));
    }

    /**
     * Tests teacher authentication through UserDetailsService.
     */
    @Test
    @DisplayName("Teacher can authenticate")
    void teacherAuthentication() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("smith@university.edu");
        
        assertEquals("smith@university.edu", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER")));
    }

    /**
     * Tests that unknown user throws exception.
     */
    @Test
    @DisplayName("Unknown user throws exception")
    void unknownUserAuthentication() {
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("unknown@test.edu"));
    }

    /**
     * Tests student course enrollment end-to-end.
     */
    @Test
    @DisplayName("Student can enroll in course")
    void studentCourseEnrollment() {
        Student student = studentRepository.findByEmail("john@student.edu").get();
        Course course = courseRepository.findById(testCourse.getId()).get();
        
        student.getCourses().add(course);
        studentRepository.save(student);
        
        Student updated = studentRepository.findByEmail("john@student.edu").get();
        assertEquals(1, updated.getCourses().size());
        assertTrue(updated.getCourses().stream()
                .anyMatch(c -> c.getTitle().equals("Java Programming")));
    }

    /**
     * Tests password encoding works correctly.
     */
    @Test
    @DisplayName("Password is encoded correctly")
    void passwordEncoding() {
        Student student = studentRepository.findByEmail("john@student.edu").get();
        assertTrue(passwordEncoder.matches("password123", student.getPassword()));
    }
}
