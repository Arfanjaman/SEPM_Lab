# Unit Tests Documentation

This document describes the unit tests for the Student-Teacher application controllers.

## Overview

**Total Tests:** 13  
**Framework:** JUnit 5 with Mockito  
**Test Type:** Unit tests with mocked dependencies

---

## StudentControllerTest (2 tests)

Tests for student course enrollment and removal functionality.

| Test Name | Description |
|-----------|-------------|
| `takeCourse` | Verifies that a student can enroll in a course. The course is added to the student's course set and saved to the database. |
| `removeCourse` | Verifies that a student can drop a course. The course is removed from the student's enrollment by matching course ID. |

---

## TeacherControllerTest (6 tests)

Tests for teacher course management and student management functionality.

| Test Name | Description |
|-----------|-------------|
| `addCourse` | Verifies that a teacher can create a new course. The course is linked to the authenticated teacher. |
| `getAllStudents` | Verifies that a teacher can retrieve the list of all students in the system. |
| `addStudent` | Verifies that a teacher can add a new student. The password is encoded before saving. |
| `addStudent_duplicateEmail` | Verifies that adding a student with an existing email returns HTTP 400 Bad Request. |
| `deleteStudent` | Verifies that a teacher can delete an existing student by ID. |
| `deleteStudent_notFound` | Verifies that deleting a non-existent student returns HTTP 400 Bad Request with "Student not found" message. |

---

## WebControllerTest (5 tests)

Tests for web views including login and dashboard pages.

| Test Name | Description |
|-----------|-------------|
| `login` | Verifies that the login endpoint returns the "login" view name. |
| `studentDashboard` | Verifies that the student dashboard loads with the student's enrolled courses and all available courses. |
| `studentDashboard_noEnrolledCourses` | Verifies that the student dashboard handles new students with no course enrollments. |
| `teacherDashboard` | Verifies that the teacher dashboard loads with the teacher's courses and calculates total credit hours (3 + 4 = 7). |
| `teacherDashboard_noCourses` | Verifies that the teacher dashboard handles teachers with no courses (total credits = 0). |

---

## Running Tests

```bash
# Run all controller tests
./mvnw test -Dtest="StudentControllerTest,TeacherControllerTest,WebControllerTest"

# Run individual test class
./mvnw test -Dtest="StudentControllerTest"

# Run all tests
./mvnw test
```

---

## Test Dependencies

- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework for isolating controller logic
- **Spring Boot Test** - Spring testing utilities
