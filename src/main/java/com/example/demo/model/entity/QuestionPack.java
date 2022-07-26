package com.example.demo.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="question_pack")
public class QuestionPack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(unique = true)
    private String code;

    @ManyToOne
    @JoinTable(name = "user_question_packs",
            joinColumns = @JoinColumn(name = "question_pack_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private User owner;

    @ManyToMany
    @JoinTable(name = "question_pack_subscriptions",
            joinColumns = @JoinColumn(name = "question_pack_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> subscribers;

    @OneToMany
    @JoinTable(name = "pack_questions",
            joinColumns = @JoinColumn(name = "question_pack_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private List<Question> questions;

    public QuestionPack(User owner, String name, String code) {
        this.owner = owner;
        this.name = name;
        this.code = code;
    }

    public void addSubscriber(User subscriber) {
        subscribers.add(subscriber);
    }


}
