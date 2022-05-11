package com.example.demo.model.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MyClassGeneralInfoDTO {
    private String className;
    private String classCode;
    private String teacherFullName;
}
