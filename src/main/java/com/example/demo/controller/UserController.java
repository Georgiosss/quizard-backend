package com.example.demo.controller;

import com.example.demo.model.dto.request.UserLoginRequestDTO;
import com.example.demo.model.dto.request.UserSignupRequestDTO;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        userLoginRequestDTO.checkRequiredFields();
        return ResponseEntity.ok(userService.authorize(userLoginRequestDTO));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserSignupRequestDTO userSignupRequestDTO) {
        userSignupRequestDTO.checkRequiredFields();
        return ResponseEntity.ok(userService.register(userSignupRequestDTO));
    }

}