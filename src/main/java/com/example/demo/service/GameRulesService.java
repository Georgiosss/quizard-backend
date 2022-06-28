package com.example.demo.service;

import com.example.demo.model.dto.response.GetRulesResponseDTO;
import com.example.demo.repository.GameRulesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameRulesService {

    @Autowired
    private GameRulesRepository gameRulesRepository;

    public List<GetRulesResponseDTO> getRules() {
        return gameRulesRepository.findAll().stream().map(
                gameRule -> new GetRulesResponseDTO(
                        gameRule.getTitle(), gameRule.getContent()
                )
        ).collect(Collectors.toList());
    }
}
