# Student-Teacher Portal - Complete Project Documentation

**Last Updated:** February 3, 2026  
**Author:** Documentation for Spring Boot Student-Teacher Management System

---

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Architecture Overview](#architecture-overview)
4. [Database Schema](#database-schema)
5. [Security Implementation](#security-implementation)
6. [Entity Layer](#entity-layer)
7. [Repository Layer](#repository-layer)
8. [Service Layer](#service-layer)
9. [Controller Layer](#controller-layer)
10. [View Layer (Templates)](#view-layer-templates)
11. [Configuration Files](#configuration-files)
12. [Authentication & Authorization Flow](#authentication--authorization-flow)
13. [User Credentials](#user-credentials)
14. [API Endpoints](#api-endpoints)

---

## 🎯 Project Overview

This is a **Role-Based Access Control (RBAC)** web application built with Spring Boot that manages interactions between Students and Teachers. The application features:

- **Separate dashboards** for Students and Teachers
- **Course management** - Teachers can create courses, Students can enroll
- **Authentication & Authorization** using Spring Security
- **BCrypt password hashing** for security
- **PostgreSQL database** for data persistence
- **Thymeleaf templates** for server-side rendering

### Key Features:
- ✅ Secure login with role-based access
- ✅ Students can view and enroll in courses
- ✅ Teachers can create courses and manage students
- ✅ Many-to-Many relationship between Students and Courses
- ✅ RESTful API endpoints for programmatic access

---

## 🛠 Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 4.0.2 |
| **Language** | Java | 17 |
| **Database** | PostgreSQL | 16 |
| **ORM** | Hibernate (JPA) | 7.2.1 |
| **Security** | Spring Security | 7.0.2 |
| **Template Engine** | Thymeleaf | 3.1.3 |
| **Build Tool** | Maven | - |
| **Server** | Apache Tomcat (Embedded) | 11.0.15 |

### Dependencies (pom.xml):
```xml
- spring-boot-starter-data-jpa      → Database access & JPA
- spring-boot-starter-security      → Authentication & Authorization
- spring-boot-starter-webmvc        → Web MVC framework
- spring-boot-starter-thymeleaf     → Template engine
- thymeleaf-extras-springsecurity6  → Security integration in templates
- postgresql                        → PostgreSQL driver
- h2                                → In-memory database (optional)
```

---

## 🏗 Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │  Login Page │  │   Student   │  │   Teacher   │         │
│  │  (Thymeleaf)│  │  Dashboard  │  │  Dashboard  │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      SECURITY LAYER                          │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Spring Security (SecurityConfig.java)                │  │
│  │  - Authentication (CustomUserDetailsService)          │  │
│  │  - Authorization (Role-based: STUDENT, TEACHER)       │  │
│  │  - BCrypt Password Encoding                           │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      CONTROLLER LAYER                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │WebController│  │   Student   │  │   Teacher   │         │
│  │   (Views)   │  │ Controller  │  │ Controller  │         │
│  │             │  │   (REST)    │  │   (REST)    │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      REPOSITORY LAYER                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   Student   │  │   Teacher   │  │   Course    │         │
│  │ Repository  │  │ Repository  │  │ Repository  │         │
│  │   (JPA)     │  │   (JPA)     │  │   (JPA)     │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                       DATABASE LAYER                         │
│                    PostgreSQL Database                       │
│    ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌──────────────┐ │
│    │ student │  │ teacher │  │ course  │  │student_courses│ │
│    │ (table) │  │ (table) │  │ (table) │  │ (join table) │ │
│    └─────────┘  └─────────┘  └─────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## 🗄 Database Schema

### 1. **student** table
```sql
CREATE TABLE student (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);
```

### 2. **teacher** table
```sql
CREATE TABLE teacher (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);
```

### 3. **course** table
```sql
CREATE TABLE course (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    credit INTEGER,
    teacher_id BIGINT,
    FOREIGN KEY (teacher_id) REFERENCES teacher(id)
);
```

### 4. **student_courses** (Join Table - Many-to-Many)
```sql
CREATE TABLE student_courses (
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES student(id),
    FOREIGN KEY (course_id) REFERENCES course(id)
);
```

### Entity Relationships:
- **Student ↔ Course**: Many-to-Many (A student can take multiple courses, a course can have multiple students)
- **Teacher ↔ Course**: One-to-Many (A teacher can teach multiple courses, each course has one teacher)

---

## 🔐 Security Implementation

### 1. SecurityConfig.java
**Location:** `src/main/java/com/example/student_teacher/config/SecurityConfig.java`

**Purpose:** Configures Spring Security for authentication and authorization.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disabled for simplicity
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (no authentication required)
                .requestMatchers("/login", "/css/**", "/js/**", "/test/**").permitAll()
                
                // Role-based access control
                .requestMatchers("/student/**").hasRole("STUDENT")  // Only STUDENT role
                .requestMatchers("/teacher/**").hasRole("TEACHER")  // Only TEACHER role
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")                    // Custom login page
                .defaultSuccessUrl("/dashboard", true)  // Redirect after login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // BCrypt for password hashing
    }
}
```

**Key Security Features:**

1. **URL-Based Authorization:**
   - `/student/**` → Requires `ROLE_STUDENT`
   - `/teacher/**` → Requires `ROLE_TEACHER`
   - `/login`, `/css/**`, `/js/**`, `/test/**` → Public access

2. **Password Encoding:**
   - Uses `BCryptPasswordEncoder` for secure password hashing
   - Passwords are never stored in plain text

3. **Form-Based Login:**
   - Custom login page at `/login`
   - Automatic redirect to `/dashboard` after successful login

### 2. CustomUserDetailsService.java
**Location:** `src/main/java/com/example/student_teacher/security/CustomUserDetailsService.java`

**Purpose:** Implements authentication by loading user details from the database.

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // Step 1: Check if email exists in STUDENT table
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            return User.builder()
                .username(student.get().getEmail())
                .password(student.get().getPassword())  // BCrypt hashed password
                .roles("STUDENT")  // Automatically becomes ROLE_STUDENT
                .build();
        }
        
        // Step 2: Check if email exists in TEACHER table
        Optional<Teacher> teacher = teacherRepository.findByEmail(email);
        if (teacher.isPresent()) {
            return User.builder()
                .username(teacher.get().getEmail())
                .password(teacher.get().getPassword())  // BCrypt hashed password
                .roles("TEACHER")  // Automatically becomes ROLE_TEACHER
                .build();
        }
        
        // Step 3: User not found
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
```

**How It Works:**
1. When user tries to login, Spring Security calls `loadUserByUsername(email)`
2. Service searches for the email in **Student** table first
3. If not found, searches in **Teacher** table
4. Returns `UserDetails` object with:
   - Username (email)
   - Password (BCrypt hash)
   - Role (STUDENT or TEACHER)
5. Spring Security compares entered password with stored hash
6. If match → Login successful → Role assigned

### Role Handling:
- **`.roles("STUDENT")`** → Internally becomes `ROLE_STUDENT` (Spring adds "ROLE_" prefix)
- **`.roles("TEACHER")`** → Internally becomes `ROLE_TEACHER`
- **`.hasRole("STUDENT")`** in SecurityConfig checks for `ROLE_STUDENT`

---

## 📦 Entity Layer

### 1. Role.java (Enum)
**Location:** `src/main/java/com/example/student_teacher/entity/Role.java`

```java
public enum Role {
    STUDENT,
    TEACHER
}
```
**Purpose:** Defines two possible user roles in the system.

---

### 2. Student.java
**Location:** `src/main/java/com/example/student_teacher/entity/Student.java`

```java
@Entity
public class Student {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    
    @Column(unique = true)
    private String email;  // Used as username for login
    
    private String password;  // BCrypt hashed
    
    @Enumerated(EnumType.STRING)
    private Role role = Role.STUDENT;  // Default role
    
    @ManyToMany
    @JoinTable(
        name = "student_courses",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();
    
    // Getters and Setters
}
```

**Key Annotations:**
- `@Entity` → JPA entity mapped to `student` table
- `@Id @GeneratedValue` → Auto-incrementing primary key
- `@Column(unique = true)` → Email must be unique (used for login)
- `@Enumerated(EnumType.STRING)` → Store enum as string ("STUDENT") not ordinal (0)
- `@ManyToMany` → Many students can take many courses
- `@JoinTable` → Defines the join table `student_courses`

---

### 3. Teacher.java
**Location:** `src/main/java/com/example/student_teacher/entity/Teacher.java`

```java
@Entity
public class Teacher {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    
    @Column(unique = true)
    private String email;  // Used as username for login
    
    private String password;  // BCrypt hashed
    
    @Enumerated(EnumType.STRING)
    private Role role = Role.TEACHER;  // Default role
    
    // Getters and Setters
}
```

**Notes:**
- Similar structure to Student
- No direct course relationship (Course has Teacher reference)

---

### 4. Course.java
**Location:** `src/main/java/com/example/student_teacher/entity/Course.java`

```java
@Entity
public class Course {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String title;
    
    private int credit;
    
    @ManyToOne
    private Teacher teacher;  // Each course belongs to one teacher
    
    // Getters and Setters
}
```

**Key Annotations:**
- `@ManyToOne` → Many courses can belong to one teacher
- Creates `teacher_id` foreign key in `course` table

---

### 5. Dept.java
**Location:** `src/main/java/com/example/student_teacher/entity/Dept.java`

```java
@Entity
public class Dept {
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    
    // Getters and Setters
}
```
**Note:** Currently not used in the application. Can be extended for department management.

---

## 🗂 Repository Layer

Repositories provide database access using Spring Data JPA. No SQL queries needed - Spring auto-generates them!

### 1. StudentRepository.java
```java
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
}
```
**Auto-Generated Methods:**
- `save(Student)` → Insert/Update student
- `findById(Long)` → Find by ID
- `findAll()` → Get all students
- `deleteById(Long)` → Delete student
- `findByEmail(String)` → **Custom method** to find student by email (used for login)

---

### 2. TeacherRepository.java
```java
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmail(String email);
}
```
**Same as StudentRepository** - used for teacher authentication.

---

### 3. CourseRepository.java
```java
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTeacher(Teacher teacher);
}
```
**Custom Method:**
- `findByTeacher(Teacher)` → Get all courses taught by a specific teacher

---

## 🎮 Controller Layer

### 1. WebController.java (Main View Controller)
**Location:** `src/main/java/com/example/student_teacher/controller/WebController.java`

**Purpose:** Handles web page rendering using Thymeleaf templates.

#### Endpoints:

| Method | URL | Function | Access |
|--------|-----|----------|--------|
| GET | `/login` | Show login page | Public |
| GET | `/dashboard` | Redirect to role-specific dashboard | Authenticated |
| GET | `/student/dashboard` | Student dashboard | ROLE_STUDENT |
| GET | `/teacher/dashboard` | Teacher dashboard | ROLE_TEACHER |
| POST | `/teacher/courses/add` | Teacher adds a course | ROLE_TEACHER |
| POST | `/student/courses/enroll/{courseId}` | Student enrolls in course | ROLE_STUDENT |
| POST | `/student/courses/drop/{courseId}` | Student drops course | ROLE_STUDENT |

#### Key Functions:

**1. Login Page:**
```java
@GetMapping("/login")
public String login() {
    return "login";  // Returns login.html template
}
```

**2. Dashboard Redirect (Role-Based):**
```java
@GetMapping("/dashboard")
public String dashboard(Authentication auth, Model model) {
    // Check user's role and redirect accordingly
    if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
        return "redirect:/student/dashboard";
    } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"))) {
        return "redirect:/teacher/dashboard";
    }
    return "redirect:/login";
}
```
**How it works:**
- `Authentication auth` → Contains logged-in user info (injected by Spring Security)
- `auth.getAuthorities()` → Returns user's roles (e.g., ROLE_STUDENT)
- Redirects to appropriate dashboard based on role

**3. Student Dashboard:**
```java
@GetMapping("/student/dashboard")
public String studentDashboard(Authentication auth, Model model) {
    // Get logged-in student by email
    Student student = studentRepository.findByEmail(auth.getName()).orElse(null);
    
    // Get all available courses
    List<Course> allCourses = courseRepository.findAll();
    
    // Add data to model (accessible in Thymeleaf template)
    model.addAttribute("student", student);
    model.addAttribute("myCourses", student != null ? student.getCourses() : List.of());
    model.addAttribute("allCourses", allCourses);
    
    return "student-dashboard";  // Returns student-dashboard.html
}
```
**Flow:**
1. Get logged-in user's email from `auth.getName()`
2. Find student in database by email
3. Load student's enrolled courses
4. Load all available courses
5. Pass data to Thymeleaf template via `Model`

**4. Teacher Dashboard:**
```java
@GetMapping("/teacher/dashboard")
public String teacherDashboard(Authentication auth, Model model) {
    Teacher teacher = teacherRepository.findByEmail(auth.getName()).orElse(null);
    List<Course> myCourses = courseRepository.findByTeacher(teacher);
    int totalCredits = myCourses.stream().mapToInt(Course::getCredit).sum();
    
    model.addAttribute("teacher", teacher);
    model.addAttribute("myCourses", myCourses);
    model.addAttribute("totalCredits", totalCredits);
    
    return "teacher-dashboard";
}
```

**5. Enroll Student in Course:**
```java
@PostMapping("/student/courses/enroll/{courseId}")
public String enrollCourse(@PathVariable Long courseId, Authentication auth) {
    Student student = studentRepository.findByEmail(auth.getName()).orElse(null);
    Course course = courseRepository.findById(courseId).orElse(null);
    
    if (student != null && course != null) {
        student.getCourses().add(course);  // Add course to student's course set
        studentRepository.save(student);    // Save to database
    }
    
    return "redirect:/student/dashboard";  // Redirect back to dashboard
}
```

---

### 2. StudentController.java (REST API)
**Location:** `src/main/java/com/example/student_teacher/controller/StudentController.java`

**Purpose:** RESTful API for student operations.

```java
@RestController
@RequestMapping("/student")
public class StudentController {
    
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
```

**API Endpoints:**
- `POST /student/courses/{courseId}` → Enroll in course (returns plain text)
- `DELETE /student/courses/{courseId}` → Drop course (returns plain text)

---

### 3. TeacherController.java (REST API)
**Location:** `src/main/java/com/example/student_teacher/controller/TeacherController.java`

**Purpose:** RESTful API for teacher operations.

```java
@RestController
@RequestMapping("/teacher")
public class TeacherController {
    
    @PostMapping("/courses")
    public Course addCourse(@RequestBody Course course, Principal principal) {
        Teacher teacher = teacherRepo.findByEmail(principal.getName()).get();
        course.setTeacher(teacher);
        return courseRepo.save(course);
    }
    
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }
    
    @PostMapping("/students")
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        if (studentRepo.findByEmail(student.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        Student savedStudent = studentRepo.save(student);
        return ResponseEntity.ok(savedStudent);
    }
    
    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        if (!studentRepo.existsById(id)) {
            return ResponseEntity.badRequest().body("Student not found");
        }
        studentRepo.deleteById(id);
        return ResponseEntity.ok("Student deleted successfully");
    }
}
```

**API Endpoints:**
- `POST /teacher/courses` → Add new course (JSON body)
- `GET /teacher/students` → Get all students (returns JSON)
- `POST /teacher/students` → Add new student (JSON body, auto-encrypts password)
- `DELETE /teacher/students/{id}` → Delete student

---

### 4. PasswordTestController.java (Testing)
**Location:** `src/main/java/com/example/student_teacher/controller/PasswordTestController.java`

**Purpose:** Utility endpoints for password testing (should be removed in production).

```java
@RestController
public class PasswordTestController {
    
    @GetMapping("/test/encode")
    public String encodePassword(@RequestParam String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
    
    @GetMapping("/test/match")
    public String testMatch(@RequestParam String plain, @RequestParam String encoded) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(plain, encoded);
        return "Matches: " + matches;
    }
}
```

**Endpoints:**
- `GET /test/encode?password=yourpass` → Generate BCrypt hash
- `GET /test/match?plain=password&encoded=hash` → Test if password matches hash

⚠️ **Security Note:** Remove these endpoints in production!

---

## 🎨 View Layer (Templates)

### 1. login.html
**Location:** `src/main/resources/templates/login.html`

**Purpose:** Login page with email/password form.

**Key Features:**
- Beautiful gradient design
- Form submits to `/login` (handled by Spring Security)
- Shows error message if login fails
- Shows success message after logout
- Displays test credentials for convenience

**Thymeleaf Integration:**
```html
<form th:action="@{/login}" method="post">
    <input type="text" name="username" placeholder="Enter your email" required>
    <input type="password" name="password" placeholder="Enter your password" required>
    <button type="submit">Login</button>
</form>

<!-- Error message -->
<div th:if="${param.error}" class="error">Invalid email or password!</div>

<!-- Logout success -->
<div th:if="${param.logout}" class="success">You have been logged out.</div>
```

---

### 2. student-dashboard.html
**Location:** `src/main/resources/templates/student-dashboard.html`

**Purpose:** Student's main interface.

**Features:**
- Welcome message with student name
- **My Enrolled Courses** - List of courses student is taking
- **All Available Courses** - List of courses to enroll in
- Enroll/Drop buttons (form actions)

**Thymeleaf Integration:**
```html
<!-- Display student name -->
<h2>Welcome, <span th:text="${student.name}">Student</span>!</h2>

<!-- Loop through enrolled courses -->
<table>
    <tr th:each="course : ${myCourses}">
        <td th:text="${course.title}">Course Title</td>
        <td th:text="${course.credit}">3</td>
        <td th:text="${course.teacher.name}">Teacher Name</td>
        <td>
            <!-- Drop course form -->
            <form th:action="@{/student/courses/drop/{id}(id=${course.id})}" method="post">
                <button type="submit" class="btn btn-danger">Drop</button>
            </form>
        </td>
    </tr>
</table>

<!-- Loop through all available courses -->
<table>
    <tr th:each="course : ${allCourses}">
        <td th:text="${course.title}">Course Title</td>
        <td>
            <!-- Enroll form -->
            <form th:action="@{/student/courses/enroll/{id}(id=${course.id})}" method="post">
                <button type="submit" class="btn btn-success">Enroll</button>
            </form>
        </td>
    </tr>
</table>
```

**Thymeleaf Syntax Explained:**
- `th:text="${variable}"` → Display variable value
- `th:each="item : ${list}"` → Loop through list
- `th:action="@{/url/{id}(id=${value})}"` → Dynamic URL with parameter
- `th:if="${condition}"` → Conditional rendering

---

### 3. teacher-dashboard.html
**Location:** `src/main/resources/templates/teacher-dashboard.html`

**Purpose:** Teacher's main interface.

**Features:**
- Welcome message with teacher name
- **My Courses** - Courses taught by teacher with total credits
- **Add New Course** - Form to create course
- **Manage Students** - Add/remove students (with JavaScript API calls)

**Key Sections:**

**1. Display Courses:**
```html
<table>
    <tr th:each="course : ${myCourses}">
        <td th:text="${course.title}">Course</td>
        <td th:text="${course.credit}">Credits</td>
    </tr>
</table>
<p>Total Credits: <strong th:text="${totalCredits}">0</strong></p>
```

**2. Add Course Form:**
```html
<form th:action="@{/teacher/courses/add}" method="post">
    <input type="text" name="title" placeholder="Course Title" required>
    <input type="number" name="credit" placeholder="Credits" required>
    <button type="submit">Add Course</button>
</form>
```

**3. Student Management (JavaScript AJAX):**
```javascript
// Fetch all students
fetch('/teacher/students')
    .then(response => response.json())
    .then(students => {
        // Display students in table
    });

// Add student
fetch('/teacher/students', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({name: name, email: email, password: password})
})
.then(response => response.json());

// Delete student
fetch(`/teacher/students/${studentId}`, {method: 'DELETE'});
```

---

## ⚙ Configuration Files

### 1. application.yaml
**Location:** `src/main/resources/application.yaml`

**Current Configuration:**
```yaml
server:
  port: 9090  # Application runs on http://localhost:9090

spring:
  application:
    name: student_teacher
  
  datasource:
    url: jdbc:postgresql://localhost:5432/university  # PostgreSQL connection
    username: jaman
    password: 2107030
  
  jpa:
    hibernate:
      ddl-auto: update  # Auto-update database schema on entity changes
    show-sql: true  # Print SQL queries in console
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
    defer-datasource-initialization: true
  
  sql:
    init:
      mode: never  # Don't run data.sql (manual SQL initialization disabled)
```

**Key Settings Explained:**

| Setting | Value | Purpose |
|---------|-------|---------|
| `server.port` | 9090 | Changed from 8080 to avoid conflicts |
| `datasource.url` | `localhost:5432/university` | Local PostgreSQL connection |
| `ddl-auto` | `update` | Hibernate auto-creates/updates tables |
| `show-sql` | `true` | Debug: see SQL queries in console |
| `init.mode` | `never` | Disabled automatic data.sql execution |

**DDL-Auto Options:**
- `create` → Drop and recreate tables on startup (data loss!)
- `update` → Update schema without data loss (recommended for dev)
- `validate` → Only check schema, don't change
- `none` → Don't do anything

---

### 2. pom.xml
**Location:** `pom.xml`

**Key Dependencies:**

```xml
<!-- Core Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Web & MVC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>

<!-- Thymeleaf Templates -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>

<!-- Databases -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

---

## 🔄 Authentication & Authorization Flow

### Complete Login Flow:

```
1. User visits http://localhost:9090/login
   ↓
2. User enters email & password
   ↓
3. Form submits to /login (POST)
   ↓
4. Spring Security intercepts request
   ↓
5. Calls CustomUserDetailsService.loadUserByUsername(email)
   ↓
6. Service searches student table for email
   ↓
7. If found → Returns UserDetails with ROLE_STUDENT
   If not found → Searches teacher table
   If found → Returns UserDetails with ROLE_TEACHER
   If not found → Throws UsernameNotFoundException
   ↓
8. Spring Security compares entered password with stored BCrypt hash
   ↓
9. If passwords match:
   - Creates Authentication object
   - Stores in SecurityContext
   - Redirects to /dashboard
   ↓
10. /dashboard checks user's role:
    - ROLE_STUDENT → Redirect to /student/dashboard
    - ROLE_TEACHER → Redirect to /teacher/dashboard
   ↓
11. User sees their dashboard (secured by role)
```

### Authorization Flow (Example: Student Dashboard):

```
1. Student tries to access /student/dashboard
   ↓
2. Spring Security checks SecurityFilterChain rules
   ↓
3. Rule: .requestMatchers("/student/**").hasRole("STUDENT")
   ↓
4. Gets Authentication from SecurityContext
   ↓
5. Checks if user has ROLE_STUDENT
   ↓
6. If YES → Allow access
   If NO → 403 Forbidden error
```

### Why Role Checking Works:

```java
// In CustomUserDetailsService
.roles("STUDENT")  // Becomes "ROLE_STUDENT" internally

// In SecurityConfig
.hasRole("STUDENT")  // Checks for "ROLE_STUDENT"

// Spring automatically adds "ROLE_" prefix!
```

---

## 👥 User Credentials

### Current Test Users:

| Role | Email | Password |
|------|-------|----------|
| Student | `student1@mail.com` | `student123` |
| Student | `student2@mail.com` | `student123` |
| Teacher | `teacher1@mail.com` | `teacher123` |
| Teacher | `teacher2@mail.com` | `teacher123` |

### Password Hashes in Database:

```sql
-- Students (password: student123)
$2a$10$eWBPJa6tjNakgYI771tJ.OPtLtSSaFdxliBdb/7UNDqs.QNgPO4Sm

-- Teachers (password: teacher123)
$2a$10$mueTw4KM6pDWXpAtWL/Ltuaam94iFjF.L06j.utsjRI.1.IY5joLi
```

### How to Add New Users:

**Option 1: Using /test/encode endpoint:**
1. Visit: `http://localhost:9090/test/encode?password=yourpassword`
2. Copy the generated hash
3. Insert into database:
```sql
INSERT INTO student (name, email, password, role)
VALUES ('John Doe', 'john@mail.com', 'PASTE_HASH_HERE', 'STUDENT');
```

**Option 2: Using Teacher Dashboard:**
- Teachers can add students programmatically via the "Manage Students" section
- Password is automatically BCrypt-encoded by `TeacherController`

---

## 🌐 API Endpoints

### Public Endpoints:
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/login` | Login page |
| POST | `/login` | Login form submission (Spring Security) |
| GET | `/logout` | Logout |

### Student Endpoints (Require ROLE_STUDENT):
| Method | URL | Description | Returns |
|--------|-----|-------------|---------|
| GET | `/student/dashboard` | Student dashboard page | HTML |
| POST | `/student/courses/enroll/{id}` | Enroll in course | Redirect |
| POST | `/student/courses/drop/{id}` | Drop course | Redirect |
| POST | `/student/courses/{id}` | Enroll (REST API) | Text |
| DELETE | `/student/courses/{id}` | Drop (REST API) | Text |

### Teacher Endpoints (Require ROLE_TEACHER):
| Method | URL | Description | Returns |
|--------|-----|-------------|---------|
| GET | `/teacher/dashboard` | Teacher dashboard page | HTML |
| POST | `/teacher/courses/add` | Add course (form) | Redirect |
| POST | `/teacher/courses` | Add course (REST API) | JSON |
| GET | `/teacher/students` | Get all students | JSON |
| POST | `/teacher/students` | Add student | JSON |
| DELETE | `/teacher/students/{id}` | Delete student | JSON |

### Testing Endpoints (Should Remove in Production):
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/test/encode?password=xxx` | Generate BCrypt hash |
| GET | `/test/match?plain=xxx&encoded=yyy` | Test password match |

---

## 🚀 Running the Application

### Prerequisites:
1. **Java 17** installed
2. **PostgreSQL 16** running locally
3. **Maven** (or use `mvnw`)

### Steps:

1. **Start PostgreSQL:**
   - Ensure PostgreSQL is running on port 5432
   - Database `university` exists
   - User `jaman` has access

2. **Run Application:**
   ```bash
   mvn spring-boot:run
   ```
   Or from IntelliJ: Run `StudentTeacherApplication.main()`

3. **Access Application:**
   - Open browser: `http://localhost:9090`
   - Login with test credentials

4. **Stopping:**
   - Press `Ctrl+C` in terminal
   - Or stop from IntelliJ

---

## 🔍 Troubleshooting

### Common Issues:

**1. Port 8080 already in use**
- **Solution:** Changed to port 9090 in `application.yaml`

**2. Cannot connect to PostgreSQL**
- **Fix:** Check if PostgreSQL is running: `Get-Service postgresql*`
- **Fix:** Verify database exists: `psql -U jaman -d university`

**3. Invalid credentials error**
- **Cause:** BCrypt hash mismatch
- **Fix:** Use `/test/encode` to generate correct hash, update database

**4. 403 Forbidden on dashboard**
- **Cause:** User doesn't have required role
- **Fix:** Check user's role in database matches endpoint requirement

**5. Tables not created**
- **Fix:** Set `ddl-auto: create` (first run only)
- **Fix:** Grant permissions: `GRANT ALL ON SCHEMA public TO jaman;`

---

## 📊 Database Quick Reference

### Check Tables:
```powershell
psql -U jaman -d university -c "\dt"
```

### View Users:
```sql
-- Students
SELECT * FROM student;

-- Teachers
SELECT * FROM teacher;
```

### Update Password:
```sql
UPDATE student SET password = 'NEW_BCRYPT_HASH' WHERE email = 'student@mail.com';
```

### View Enrollments:
```sql
SELECT s.name, c.title 
FROM student s
JOIN student_courses sc ON s.id = sc.student_id
JOIN course c ON c.id = sc.course_id;
```

---

## 📝 Future Enhancements

### Potential Features:
- [ ] Email verification for new users
- [ ] Password reset functionality
- [ ] Course prerequisites
- [ ] Grade management
- [ ] Department integration
- [ ] File uploads (assignments)
- [ ] Real-time notifications
- [ ] Admin role for system management
- [ ] Student profile pages
- [ ] Course ratings/reviews

---

## 🔒 Security Best Practices

### Current Implementation:
✅ BCrypt password hashing  
✅ Role-based access control  
✅ CSRF protection (can be enabled)  
✅ Secure password storage  

### Production Recommendations:
1. **Enable CSRF** protection (remove `.csrf().disable()`)
2. **Remove** `/test/**` endpoints
3. **Use HTTPS** in production
4. **Add** rate limiting for login attempts
5. **Implement** session timeout
6. **Add** audit logging
7. **Use** environment variables for credentials (not hardcoded)

---

## 📚 Key Concepts Summary

### Spring Security Flow:
```
User Login → CustomUserDetailsService → Database Lookup → 
Password Match → Authentication Object → SecurityContext → 
Role-Based Access Control → Dashboard
```

### Role System:
- **Database:** Stores `"STUDENT"` or `"TEACHER"` as string
- **Spring Security:** Converts to `ROLE_STUDENT`, `ROLE_TEACHER`
- **Authorization:** Checks if user has required `ROLE_*`

### JPA Relationships:
- **Student ↔ Course:** `@ManyToMany` with join table
- **Teacher ↔ Course:** `@OneToMany` (Teacher) + `@ManyToOne` (Course)

### MVC Pattern:
- **Model:** Entities (Student, Teacher, Course)
- **View:** Thymeleaf templates (HTML)
- **Controller:** WebController, StudentController, TeacherController

---

## 📄 File Structure Summary

```
student_teacher/
├── src/main/
│   ├── java/com/example/student_teacher/
│   │   ├── StudentTeacherApplication.java       # Main entry point
│   │   ├── config/
│   │   │   └── SecurityConfig.java              # Security configuration
│   │   ├── controller/
│   │   │   ├── WebController.java               # View rendering
│   │   │   ├── StudentController.java           # Student REST API
│   │   │   ├── TeacherController.java           # Teacher REST API
│   │   │   └── PasswordTestController.java      # Testing utility
│   │   ├── entity/
│   │   │   ├── Student.java                     # Student entity
│   │   │   ├── Teacher.java                     # Teacher entity
│   │   │   ├── Course.java                      # Course entity
│   │   │   ├── Dept.java                        # Department entity
│   │   │   └── Role.java                        # Role enum
│   │   ├── repository/
│   │   │   ├── StudentRepository.java           # Student DB access
│   │   │   ├── TeacherRepository.java           # Teacher DB access
│   │   │   └── CourseRepository.java            # Course DB access
│   │   └── security/
│   │       └── CustomUserDetailsService.java    # Authentication
│   └── resources/
│       ├── templates/
│       │   ├── login.html                       # Login page
│       │   ├── student-dashboard.html           # Student UI
│       │   └── teacher-dashboard.html           # Teacher UI
│       ├── application.yaml                     # Configuration
│       └── data.sql                             # Initial data (disabled)
├── pom.xml                                      # Maven dependencies
├── docker-compose.yml                           # Docker setup (optional)
├── Dockerfile                                   # Docker image (optional)
└── PROJECT_DOCUMENTATION.md                     # This file
```

---

**End of Documentation**  
*This document will be updated as the project evolves.*

---

## Quick Reference Card

### Login:
```
URL: http://localhost:9090/login
Students: student1@mail.com / student123
Teachers: teacher1@mail.com / teacher123
```

### Database:
```powershell
# Connect
psql -U jaman -d university

# Common queries
\dt                    # List tables
SELECT * FROM student; # View students
SELECT * FROM teacher; # View teachers
```

### Generate Password Hash:
```
http://localhost:9090/test/encode?password=yourpassword
```

### Stop Application:
```
Ctrl + C (terminal)
Stop button (IntelliJ)
```
