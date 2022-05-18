package com.example.demo.controller;


import com.example.demo.model.dto.request.AddQuestionsRequestDTO;
import com.example.demo.model.dto.response.CreateClassResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/questions-management")
public class QuestionsManagementController {

    @PostMapping("/add-questions")
    public ResponseEntity<?> createClass(@RequestParam("file") MultipartFile file) {
        System.out.println(file.getName());
        return ResponseEntity.ok("OK");
    }
}
