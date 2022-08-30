package com.example.demo.model.dto.response;

import lombok.Data;
import org.apache.commons.math3.util.Pair;

import java.util.List;

@Data
public class CreateResponseDTO {
    private List<Pair<String, List<String>>> gameDistribution;
}
