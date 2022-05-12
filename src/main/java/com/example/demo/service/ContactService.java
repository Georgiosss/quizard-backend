package com.example.demo.service;

import com.example.demo.model.dto.response.ContactGetInfoDTO;
import com.example.demo.model.entity.Contact;
import com.example.demo.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public List<ContactGetInfoDTO> getAllContacts() {
        List<Contact> contacts = contactRepository.findAll();

        return contacts.stream().map(
                c -> new ContactGetInfoDTO(c.getFullName(), c.getEmail(),
                        c.getFacebookLink(), c.getLinkedinLink(),
                        c.getImageName()
                )
        ).collect(Collectors.toList());
    }
}
