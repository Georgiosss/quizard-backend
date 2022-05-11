package com.example.demo.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AboutUsGetInfoResponseDTO {
    private String title;
    private String subtitle;
    private String content;
}
