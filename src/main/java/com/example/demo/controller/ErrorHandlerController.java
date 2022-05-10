package com.example.demo.controller;

import com.example.demo.model.dto.exception.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice(value = "com.example.demo.controller")
public class ErrorHandlerController {


    @ResponseBody
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> catchError(ApiException apiException) {
        return ResponseEntity.badRequest().body(apiException.getKeyword());
    }

}