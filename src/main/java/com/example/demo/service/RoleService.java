package com.example.demo.service;

import com.example.demo.model.dto.exception.ApiException;
import com.example.demo.model.entity.Role;
import com.example.demo.model.enums.ERole;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role getRoleByName(ERole roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ApiException("Error: Role is not found."));
    }
}
