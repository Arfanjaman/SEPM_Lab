-- Create tables manually
CREATE TABLE IF NOT EXISTS student (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS teacher (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS course (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    code VARCHAR(50),
    teacher_id BIGINT,
    FOREIGN KEY (teacher_id) REFERENCES teacher(id)
);

CREATE TABLE IF NOT EXISTS student_courses (
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES student(id),
    FOREIGN KEY (course_id) REFERENCES course(id)
);

-- Insert Students (password: student123)
INSERT INTO student (name, email, password, role)
VALUES 
    ('Alice Johnson', 'student1@mail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J7Cz8eZwpB5lW3Y5qKQYCqxXNqN1hO', 'STUDENT'),
    ('Bob Smith', 'student2@mail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J7Cz8eZwpB5lW3Y5qKQYCqxXNqN1hO', 'STUDENT')
ON CONFLICT (email) DO NOTHING;

-- Insert Teachers (password: teacher123)
INSERT INTO teacher (name, email, password, role)
VALUES 
    ('Dr. Emma Wilson', 'teacher1@mail.com', '$2a$10$xN9qo8uLOickgx2ZMRZoMye1J7Cz8eZwpB5lW3Y5qKQYCqxXNqN1hP', 'TEACHER'),
    ('Prof. Michael Brown', 'teacher2@mail.com', '$2a$10$xN9qo8uLOickgx2ZMRZoMye1J7Cz8eZwpB5lW3Y5qKQYCqxXNqN1hP', 'TEACHER')
ON CONFLICT (email) DO NOTHING;
