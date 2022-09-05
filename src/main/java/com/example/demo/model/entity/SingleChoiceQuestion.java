package com.example.demo.model.entity;

import com.example.demo.model.enums.QuestionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "single_choice_questions")
public class SingleChoiceQuestion extends Question {

    @JsonIgnore
    @Column
    private Double answer;


    public SingleChoiceQuestion(String question, Double answer, Long time) {
        super(question, time, QuestionType.SINGLE);
        this.answer = answer;
    }
}
