package com.example.demo.repository;

import com.example.demo.model.entity.QuestionPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionPackRepository extends JpaRepository<QuestionPack, Long> {

    Optional<QuestionPack> findByCode(String code);

    boolean existsByCode(String code);
}
