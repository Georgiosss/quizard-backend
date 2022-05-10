package com.example.demo.controller;

import com.example.demo.model.dto.request.UserLoginRequestDTO;
import com.example.demo.model.dto.request.UserSignupRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        return null;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserSignupRequestDTO userSignupRequestDTO) {
        return null;
    }

}