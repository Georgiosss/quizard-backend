package com.example.demo.model.dto.request;

import com.example.demo.model.dto.ValidDTO;
import com.example.demo.model.dto.exception.ApiException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSignupRequestDTO implements ValidDTO {
    private String email;
    private String password;
    private String fullName;
    private List<String> roles;

    @Override
    public void checkRequiredFields() {
        if (email == null || password == null || fullName == null) {
            throw new ApiException("Invalid arguments!");
        }
    }
}
