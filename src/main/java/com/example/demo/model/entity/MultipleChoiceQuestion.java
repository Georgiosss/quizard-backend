package com.example.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "multiple_choice_questions")
public class MultipleChoiceQuestion extends Question {

    @OneToMany
    private List<Choice> choices;

    @JsonIgnore
    @Column
    private Integer answer;

    public MultipleChoiceQuestion(String question, List<Choice> choices, Integer answer, Long time) {
        super(question, time);
        this.choices = choices;
        this.answer = answer;
    }

}