package com.example.demo.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String question;

    @JsonIgnore
    @Column
    private Long time;

    @JsonIgnore
    @ManyToOne
    @JoinTable(name = "pack_questions",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "question_pack_id"))
    private QuestionPack pack;

    public Question(String question, Long time) {
        this.question = question;
        this.time = time;
    }

}
