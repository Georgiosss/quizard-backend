package com.example.demo.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContactGetInfoDTO {
    private String fullName;
    private String email;
    private String facebookLink;
    private String linkedinLink;
    private String imageName;
}
