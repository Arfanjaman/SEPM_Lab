-- ========================================
-- STUDENTS (password: student123)
-- ========================================
INSERT INTO student (id, name, email, password, role)
VALUES (1, 'Alice Johnson', 'student1@mail.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMye1J7Cz8eZwpB5lW3Y5qKQYCqxXNqN1hO', 'STUDENT');

INSERT INTO student (id, name, email, password, role)
VALUES (2, 'Bob Smith', 'student2@mail.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMye1J7Cz8eZwpB5lW3Y5qKQYCqxXNqN1hO', 'STUDENT');

-- ========================================
-- TEACHERS (password: teacher123)
-- ========================================
INSERT INTO teacher (id, name, email, password, role)
VALUES (1, 'Dr. Emma Wilson', 'teacher1@mail.com',
        '$2a$10$xN9qo8uLOickgx2ZMRZoMye1J7Cz8eZwpB5lW3Y5qKQYCqxXNqN1hP', 'TEACHER');

INSERT INTO teacher (id, name, email, password, role)
VALUES (2, 'Prof. Michael Brown', 'teacher2@mail.com',
        '$2a$10$xN9qo8uLOickgx2ZMRZoMye1J7Cz8eZwpB5lW3Y5qKQYCqxXNqN1hP', 'TEACHER');

-- ========================================
-- CREDENTIALS REFERENCE:
-- Students: email=student1@mail.com or student2@mail.com, password=student123
-- Teachers: email=teacher1@mail.com or teacher2@mail.com, password=teacher123
-- ========================================