package com.example.demo.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtDTO {
    private String token;
    private Long id;
    private String email;
    private List<String> roles;
}
