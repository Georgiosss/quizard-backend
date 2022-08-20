package com.example.demo.service;

import com.example.demo.model.entity.Choice;
import com.example.demo.repository.ChoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChoiceService {

    @Autowired
    private ChoiceRepository choiceRepository;

    public void save(Choice answer) {
        choiceRepository.save(answer);
    }

    public void saveAll(List<Choice> answers) {
        choiceRepository.saveAll(answers);
    }
}
