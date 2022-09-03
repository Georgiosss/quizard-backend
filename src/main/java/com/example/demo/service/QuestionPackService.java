package com.example.demo.service;

import com.example.demo.model.dto.exception.ApiException;
import com.example.demo.model.entity.QuestionPack;
import com.example.demo.repository.QuestionPackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionPackService {

    @Autowired
    private QuestionPackRepository questionPackRepository;

    public QuestionPack getByCode(String code) {
        return questionPackRepository.findByCode(code).orElseThrow(
                () -> new ApiException("Question Pack not found")
        );
    }
}
