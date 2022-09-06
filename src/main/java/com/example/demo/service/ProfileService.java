package com.example.demo.service;

import com.example.demo.model.dto.exception.ApiException;
import com.example.demo.model.dto.request.EditProfileRequestDTO;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.ERole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    public EditProfileRequestDTO getProfile() {
        User user = userService.getAuthenticatedUser();

        List<String> roles = user.getRoles().stream().map(
                role -> role.getName().toString()
        ).collect(Collectors.toList());

        return new EditProfileRequestDTO(user.getFullName(), user.getEmail(), roles);
    }

    public void editProfile(String fullName, String email, List<String> rolesStr) {
        if (fullName.isEmpty() || email.isEmpty() || rolesStr.isEmpty()) {
            throw new ApiException("Not enough information");
        }

        Set<Role> roles = rolesStr.stream().map(
                roleStr -> roleService.getRoleByName(ERole.fromValue(roleStr))
        ).collect(Collectors.toSet());

        User user = userService.getAuthenticatedUser();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRoles(roles);

        userService.saveUser(user);
    }
}
