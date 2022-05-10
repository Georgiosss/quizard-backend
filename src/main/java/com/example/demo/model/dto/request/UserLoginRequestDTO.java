package com.example.demo.model.dto.request;

import com.example.demo.model.dto.ValidDTO;
import com.example.demo.model.dto.exception.ApiException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserLoginRequestDTO implements ValidDTO {
    private String email;
    private String password;

    @Override
    public void checkRequiredFields() {
        if (email == null || password == null) {
            throw new ApiException("Invalid arguments!");
        }
    }

}
