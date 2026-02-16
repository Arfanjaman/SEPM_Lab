package com.example.student_teacher.security;

import com.example.student_teacher.entity.Student;
import com.example.student_teacher.entity.Teacher;
import com.example.student_teacher.repository.StudentRepository;
import com.example.student_teacher.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomUserDetailsService.
 * Tests authentication logic for students and teachers.
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private Student testStudent;
    private Teacher testTeacher;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setName("John Doe");
        testStudent.setEmail("john@student.edu");
        testStudent.setPassword("encodedPassword");

        testTeacher = new Teacher();
        testTeacher.setName("Dr. Smith");
        testTeacher.setEmail("smith@university.edu");
        testTeacher.setPassword("encodedPassword");
    }

    /**
     * Verifies that a student can be loaded by email.
     */
    @Test
    @DisplayName("Load student by email")
    void loadUserByUsername_student() {
        when(studentRepository.findByEmail("john@student.edu")).thenReturn(Optional.of(testStudent));

        UserDetails userDetails = userDetailsService.loadUserByUsername("john@student.edu");

        assertEquals("john@student.edu", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));
    }

    /**
     * Verifies that a teacher can be loaded by email.
     */
    @Test
    @DisplayName("Load teacher by email")
    void loadUserByUsername_teacher() {
        when(studentRepository.findByEmail("smith@university.edu")).thenReturn(Optional.empty());
        when(teacherRepository.findByEmail("smith@university.edu")).thenReturn(Optional.of(testTeacher));

        UserDetails userDetails = userDetailsService.loadUserByUsername("smith@university.edu");

        assertEquals("smith@university.edu", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER")));
    }

    /**
     * Verifies that exception is thrown for non-existent user.
     */
    @Test
    @DisplayName("Throw exception for unknown user")
    void loadUserByUsername_notFound() {
        when(studentRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
        when(teacherRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername("unknown@test.com"));
    }

    /**
     * Verifies that student lookup happens before teacher lookup.
     */
    @Test
    @DisplayName("Student lookup takes priority")
    void loadUserByUsername_studentPriority() {
        when(studentRepository.findByEmail("john@student.edu")).thenReturn(Optional.of(testStudent));

        userDetailsService.loadUserByUsername("john@student.edu");

        verify(studentRepository).findByEmail("john@student.edu");
        verify(teacherRepository, never()).findByEmail(anyString());
    }
}
