package com.example.demo.service;

import com.example.demo.model.entity.Territory;
import com.example.demo.repository.TerritoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TerritoryService {

    @Autowired
    private TerritoryRepository territoryRepository;

    public List<Territory> getAllTerritories() {
        return territoryRepository.findAll();
    }
}
