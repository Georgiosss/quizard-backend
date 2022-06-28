package com.example.demo.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "classes")
public class Class {
    @Id
    @Column(name = "class_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classId;

    @Column
    private String className;

    @Column()
    private String classCode;

    @ManyToOne
    @JoinTable(name = "class_teacher",
            joinColumns = @JoinColumn(name = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private User teacher;

    @ManyToMany
    @JoinTable(name = "class_members",
            joinColumns = @JoinColumn(name = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> members;

    public Class(String classCode, String className, User authenticatedUser) {
        this.classCode = classCode;
        this.className = className;
        this.teacher = authenticatedUser;
    }

    public Class() {
    }
}
