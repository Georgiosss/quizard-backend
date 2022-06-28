package com.example.demo.repository;

import com.example.demo.model.entity.Class;
import com.example.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByClassCode(String classCode);

    boolean existsByClassCode(String classCode);

    List<Class> findAllByTeacher(User teacher);
}
