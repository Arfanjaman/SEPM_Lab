# Integration Tests Documentation

## Overview

Integration tests verify that multiple components of the application work together correctly. Unlike unit tests that mock dependencies, integration tests use real implementations with an in-memory database.

**Test File:** `src/test/java/com/example/student_teacher/integration/StudentTeacherIntegrationTest.java`  
**Total Tests:** 8  
**Database:** H2 In-Memory  

---

## How It Works

### Configuration

1. **`@SpringBootTest`** - Loads the full Spring application context with all beans
2. **`@ActiveProfiles("test")`** - Uses `application-test.yaml` configuration
3. **`@Transactional`** - Each test runs in a transaction that rolls back after completion

### Test Database

Instead of connecting to PostgreSQL (production), tests use H2 in-memory database:

```yaml
# src/test/resources/application-test.yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop  # Creates tables at start, drops at end
```

### Test Setup

Before each test, the `@BeforeEach` method:
1. Clears all existing data
2. Creates a test teacher with encoded password
3. Creates a test student with encoded password
4. Creates a test course linked to the teacher

---

## Tests

| Test | Description |
|------|-------------|
| `studentPersistence` | Verifies student entity is saved and retrieved from database |
| `teacherPersistence` | Verifies teacher entity is saved and retrieved from database |
| `coursePersistence` | Verifies course is linked to teacher via foreign key |
| `studentAuthentication` | Verifies student loads via UserDetailsService with ROLE_STUDENT |
| `teacherAuthentication` | Verifies teacher loads via UserDetailsService with ROLE_TEACHER |
| `unknownUserAuthentication` | Verifies UsernameNotFoundException for invalid emails |
| `studentCourseEnrollment` | Tests full enrollment flow: student → course (many-to-many) |
| `passwordEncoding` | Verifies BCrypt password encoding works correctly |

---

## What Integration Tests Validate

1. **Database Layer** - JPA entities, repositories, and relationships work correctly
2. **Security Layer** - Authentication service loads users with correct roles
3. **Password Encoding** - BCrypt encoder integrates properly with the system
4. **Entity Relationships** - Many-to-many (student-course), many-to-one (course-teacher)

---

## Running Integration Tests

```bash
# Run only integration tests
./mvnw test -Dtest="StudentTeacherIntegrationTest"

# Run all tests (unit + integration)
./mvnw test
```

---

## Key Differences: Unit vs Integration Tests

| Aspect | Unit Tests | Integration Tests |
|--------|-----------|-------------------|
| Dependencies | Mocked (Mockito) | Real beans |
| Database | None | H2 in-memory |
| Speed | Fast (~3s) | Slower (~15s) |
| Scope | Single class/method | Multiple layers |
| Spring Context | No | Full context loaded |
