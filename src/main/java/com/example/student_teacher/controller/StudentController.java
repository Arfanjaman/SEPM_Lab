package com.example.student_teacher.controller;



import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import com.example.student_teacher.repository.*;
import com.example.student_teacher.entity.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;

    public StudentController(StudentRepository studentRepo, CourseRepository courseRepo) {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
    }

    @PostMapping("/courses/{courseId}")
    public String takeCourse(@PathVariable Long courseId, Principal principal) {
        Student student = studentRepo.findByEmail(principal.getName()).get();
        Course course = courseRepo.findById(courseId).get();
        student.getCourses().add(course);
        studentRepo.save(student);
        return "Course taken";
    }

    @DeleteMapping("/courses/{courseId}")
    public String removeCourse(@PathVariable Long courseId, Principal principal) {
        Student student = studentRepo.findByEmail(principal.getName()).get();
        student.getCourses().removeIf(c -> c.getId().equals(courseId));
        studentRepo.save(student);
        return "Course removed";
    }
}
