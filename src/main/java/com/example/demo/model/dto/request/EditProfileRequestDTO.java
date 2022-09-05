package com.example.demo.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EditProfileRequestDTO {
    private String username;
    private String email;
    private List<String> roles;
}
