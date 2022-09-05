package com.example.demo.controller;


import com.example.demo.model.dto.request.EditProfileRequestDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping("/get-profile")
    public ResponseEntity<?> getProfile() {
        List<String> roles = new ArrayList<>();
        roles.add("სტუდენტი");
        return ResponseEntity.ok(new EditProfileRequestDTO("გიორგი ნიგალიძე", "gniga18@freeuni.edu.ge", roles));
    }

    @PutMapping("/edit-profile")
    public ResponseEntity<?> EditProfile(@RequestBody EditProfileRequestDTO request) {
        return null;
    }
}
