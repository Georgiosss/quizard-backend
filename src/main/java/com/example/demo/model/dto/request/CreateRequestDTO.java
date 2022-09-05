package com.example.demo.model.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateRequestDTO {
    private String questionPackCode;
    private List<Long> userIds;
}

