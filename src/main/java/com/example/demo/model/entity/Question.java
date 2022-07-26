package com.example.demo.model.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String question;

    @Column
    private String answer;

    @Column
    private Long time;

    @ManyToOne
    @JoinTable(name = "pack_questions",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "question_pack_id"))
    private QuestionPack pack;

    public Question(String question, String answer, Long time) {
        this.question = question;
        this.answer = answer;
        this.time = time;
    }

}
