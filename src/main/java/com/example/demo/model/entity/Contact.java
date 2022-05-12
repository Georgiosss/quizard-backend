package com.example.demo.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String fullName;

    @Column
    private String email;

    @Column
    private String facebookLink;

    @Column
    private String linkedinLink;

    @Column
    private String imageName;
}