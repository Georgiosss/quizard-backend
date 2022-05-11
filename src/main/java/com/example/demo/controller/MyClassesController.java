package com.example.demo.controller;


import com.example.demo.model.dto.response.ClassMemberDTO;
import com.example.demo.model.dto.response.ContactGetInfoDTO;
import com.example.demo.model.dto.response.MyClassGeneralInfoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/my-classes")
public class MyClassesController {



    @GetMapping("/get-classes")
    public ResponseEntity<?> getInfo() {
        List<MyClassGeneralInfoDTO> classes = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            classes.add(new MyClassGeneralInfoDTO("კლასი " + i, "" + i, "მასწავლებლის სახელი გვარი " + i));
        }
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/class/{classCode}")
    public ResponseEntity<?> getClassMembers(@PathVariable String classCode) {
        List<ClassMemberDTO> classMembers = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            classMembers.add(new ClassMemberDTO(i, "სახელი გვარი " + i));
        }
        return ResponseEntity.ok(classMembers);
    }

    @PostMapping("/join-class")
    public ResponseEntity<?> joinClass(@RequestBody String classCode) {
        System.out.println(classCode);
        return ResponseEntity.ok("აქ დამიბრუნე კლასის სახელი");
    }

    @PostMapping("/create-class")
    public ResponseEntity<?> createClass(@RequestBody String className) {
        System.out.println(className);
        return ResponseEntity.ok("აქ დამიბრუნე კლასის კოდი");
    }
}
