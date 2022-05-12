package com.example.demo.controller;


import com.example.demo.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/my-classes")
public class MyClassesController {

    @Autowired
    private ClassService classService;

    @GetMapping("/get-classes")
    public ResponseEntity<?> getInfo() {
        return ResponseEntity.ok(classService.getAllClasses());
    }

    @GetMapping("/class/{classCode}")
    public ResponseEntity<?> getClassMembers(@PathVariable String classCode) {
        return ResponseEntity.ok(classService.getClassMembers(classCode));
    }

    @PostMapping("/join-class")
    public ResponseEntity<?> joinClass(@RequestBody String classCode) {
        return ResponseEntity.ok(classService.joinClass(classCode));
    }

    @PostMapping("/create-class")
    public ResponseEntity<?> createClass(@RequestBody String className) {
        return ResponseEntity.ok(classService.createClass(className));
    }
}
