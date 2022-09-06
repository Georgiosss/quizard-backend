package com.example.demo.controller;


import com.example.demo.model.dto.request.EditProfileRequestDTO;
import com.example.demo.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/get-profile")
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.ok(profileService.getProfile());
    }

    @PutMapping("/edit-profile")
    public ResponseEntity<?> EditProfile(@RequestBody EditProfileRequestDTO request) {
        profileService.editProfile(request.getFullName(), request.getEmail(), request.getRoles());
        return ResponseEntity.ok("");
    }
}
