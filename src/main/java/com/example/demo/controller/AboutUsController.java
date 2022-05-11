package com.example.demo.controller;

import com.example.demo.model.dto.request.UserLoginRequestDTO;
import com.example.demo.model.dto.response.AboutUsGetInfoResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/about-us")
public class AboutUsController {

    @GetMapping("/get-info")
    public ResponseEntity<?> authenticateUser() {
        return ResponseEntity.ok(new AboutUsGetInfoResponseDTO("საბაკალავრო პროექტი", "MACS18", "ჩვენ ვართ თავისუფალი უნივერსიტეტის MACS-ის სკოლის" +
                "სტუდენტები. საბაკალავრო პროექტად გადავწყვიტეთ გაგვეკეთებინა თამაში, სახელად - Quizard. საბა ცოტა მოიფიქრე ტექსტი და დაამატე აქა"));
    }
}
