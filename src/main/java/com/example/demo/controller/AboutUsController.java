package com.example.demo.controller;

import com.example.demo.model.dto.request.UserLoginRequestDTO;
import com.example.demo.model.dto.response.AboutUsGetInfoResponseDTO;
import com.example.demo.service.AboutUsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/about-us")
public class AboutUsController {

    @Autowired
    private AboutUsService aboutUsService;

    @GetMapping("/get-info")
    public ResponseEntity<?> getInfo() {
        return ResponseEntity.ok(aboutUsService.getAboutUsInfo());
    }
}
