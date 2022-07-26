package com.example.demo.controller;


import com.example.demo.service.QuestionsManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/questions-management")
public class QuestionsManagementController {

    @Autowired
    private QuestionsManagementService questionsManagementService;

    @PostMapping("/add-questions")
    public ResponseEntity<?> addQuestions(@RequestParam("file") MultipartFile file, @RequestParam("questionsName") String questionsName) {
        return ResponseEntity.ok(questionsManagementService.addQuestions(questionsName, file));
    }

    @PostMapping("/import-questions")
    public ResponseEntity<?> importQuestions(@RequestBody String questionsCode) {
        return ResponseEntity.ok(questionsManagementService.importQuestions(questionsCode));
    }

    @GetMapping("/get-all-questions")
    public ResponseEntity<?> getQuestions() {
        return ResponseEntity.ok(questionsManagementService.getQuestionPacks());
    }

    @GetMapping("/get-questions-by-code/{questionsCode}")
    public ResponseEntity<?> getQuestionsByCode(@PathVariable String questionsCode) {
        return ResponseEntity.ok(questionsManagementService.getQuestionsByCode(questionsCode));
    }
}
