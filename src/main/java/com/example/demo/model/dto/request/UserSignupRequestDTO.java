package com.example.demo.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSignupRequestDTO {
    private String email;
    private String password;
    private String fullName;
    private List<String> roles;

}
