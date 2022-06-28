package com.example.demo.repository;

import com.example.demo.model.entity.GameRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRulesRepository extends JpaRepository<GameRule, Long> {
}
