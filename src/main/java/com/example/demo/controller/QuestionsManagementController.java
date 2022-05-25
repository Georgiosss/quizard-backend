package com.example.demo.controller;


import com.example.demo.model.dto.response.AddQuestionsResponseDTO;
import com.example.demo.model.dto.response.GetQuestionsByCodeResponseDTO;
import com.example.demo.model.dto.response.GetQuestionsResponseDTO;
import com.example.demo.model.dto.response.ImportQuestionsResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/get-all-questions")
    public ResponseEntity<?> getQuestions() {
        List<GetQuestionsResponseDTO> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new GetQuestionsResponseDTO("questionsName " + i, "questionsCode " + i));
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/get-questions-by-code/{questionsCode}")
    public ResponseEntity<?> getQuestionsByCode(@PathVariable String questionsCode) {
        System.out.println(questionsCode);
        List<GetQuestionsByCodeResponseDTO> list = new ArrayList<>();
        for (long i = 0; i < 10; i++) {
            list.add(new GetQuestionsByCodeResponseDTO(i, "question  " + i));
        }
        return ResponseEntity.ok(list);
    }
}
