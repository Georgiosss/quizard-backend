package com.example.demo.service;

import com.example.demo.model.dto.response.AboutUsGetInfoResponseDTO;
import com.example.demo.model.entity.AboutUs;
import com.example.demo.repository.AboutUsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AboutUsService {

    @Autowired
    private AboutUsRepository aboutUsRepository;

    public AboutUsGetInfoResponseDTO getAboutUsInfo() {
        AboutUs aboutUs = aboutUsRepository.getById(1L);

        return new AboutUsGetInfoResponseDTO(
                aboutUs.getTitle(), aboutUs.getSubtitle(), aboutUs.getContent()
        );
    }
}
