package com.example.demo.repository;

import com.example.demo.model.entity.SingleChoiceQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SingleChoiceQuestionRepository extends JpaRepository<SingleChoiceQuestion, Long> {
}
