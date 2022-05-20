package com.example.demo.controller;


import com.example.demo.model.dto.response.AddQuestionsResponseDTO;
import com.example.demo.model.dto.response.ImportQuestionsResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/questions-management")
public class QuestionsManagementController {

    @PostMapping("/add-questions")
    public ResponseEntity<?> addQuestions(@RequestParam("file") MultipartFile file, @RequestParam("questionsName") String questionsName) {
        System.out.println(file.getName());
        System.out.println(questionsName);
        return ResponseEntity.ok(new AddQuestionsResponseDTO("რაიმე უნიკალური კოდი ჩაუწერე აქ, " +
                "მერე კითხვების იმპორტის დროს სხვებისგან ამ კოდს გამოიყენებენ"));
    }

    @PostMapping("/import-questions")
    public ResponseEntity<?> importQuestions(@RequestBody String questionsCode) {
        System.out.println(questionsCode);
        return ResponseEntity.ok(new ImportQuestionsResponseDTO("აქ პროსტა სახელი დამიბრუნე რაც ზედა მეთოდში მოგეცი"));
    }
}
