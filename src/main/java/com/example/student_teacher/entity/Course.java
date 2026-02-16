package com.example.student_teacher.entity;


import jakarta.persistence.*;

@Entity
public class Course {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private int credit;

    @ManyToOne
    private Teacher teacher;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public int getCredit() { return credit; }
    public Teacher getTeacher() { return teacher; }

    public void setTitle(String title) { this.title = title; }
    public void setCredit(int credit) { this.credit = credit; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
}
