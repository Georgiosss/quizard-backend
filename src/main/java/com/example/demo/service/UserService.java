package com.example.demo.service;

import com.example.demo.config.JwtUtils;
import com.example.demo.model.dto.exception.ApiException;
import com.example.demo.model.dto.request.UserLoginRequestDTO;
import com.example.demo.model.dto.request.UserSignupRequestDTO;
import com.example.demo.model.dto.response.JwtDTO;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.ERole;
import com.example.demo.model.enums.Errors;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtils jwtUtils;

    public JwtDTO authorize(UserLoginRequestDTO dto) {
        Authentication authentication = authenticateUser(dto.getEmail(), dto.getPassword());
        String token = "Bearer " + jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtDTO(
                token,
                userDetails.getId(),
                userDetails.getEmail(),
                roles
        );
    }

    public JwtDTO register(UserSignupRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException(Errors.EMAIL_USED.getValue());
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .build();

        addRoles(user, dto.getRoles());

        userRepository.save(user);

        Authentication authentication = authenticateUser(dto.getEmail(), dto.getPassword());

        String token = "Bearer " + jwtUtils.generateJwtToken(authentication);

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        return new JwtDTO(
                token,
                user.getUserId(),
                user.getEmail(),
                roles
        );
    }

    private Authentication authenticateUser(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    private void addRoles(User user, List<String> strRoles) {
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            switch (role) {
                case "STUDENT":
                    Role studentRole = roleService.getRoleByName(ERole.ROLE_STUDENT);
                    roles.add(studentRole);
                    break;
                case "TEACHER":
                    Role teacherRole = roleService.getRoleByName(ERole.ROLE_TEACHER);
                    roles.add(teacherRole);
                    break;
                default: break;
            }
        });

        user.setRoles(roles);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found!"));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found!"));
    }

}
