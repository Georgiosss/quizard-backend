package com.example.demo.controller;


import com.example.demo.model.dto.response.AboutUsGetInfoResponseDTO;
import com.example.demo.model.dto.response.ContactGetInfoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/contact")
public class ContactController {


    @GetMapping("/get-info")
    public ResponseEntity<?> getInfo() {
        List<ContactGetInfoDTO> contacts = new ArrayList<>();
        contacts.add(new ContactGetInfoDTO("გიორგი ნიგალიძე",
                "gniga18@freeuni.edu.ge",
                "https://www.facebook.com/georgios.nigalius",
                "https://www.linkedin.com/in/giorgi-nigalidze-80a16a1b5",
                "gniga18.png"));
        contacts.add(new ContactGetInfoDTO("საბა ცერცვაძე",
                "stser18@freeuni.edu.ge",
                "https://www.facebook.com/saba.cercvadze",
                "https://www.linkedin.com/in/saba-tsertsvadze",
                "stser18.png"));
        return ResponseEntity.ok(contacts);
    }
}
