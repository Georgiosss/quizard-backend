package com.example.demo.service;

import com.example.demo.model.dto.exception.ApiException;
import com.example.demo.model.dto.response.ClassMemberDTO;
import com.example.demo.model.dto.response.MyClassGeneralInfoDTO;
import com.example.demo.model.entity.Class;
import com.example.demo.model.entity.User;
import com.example.demo.repository.ClassRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private UserRepository userRepository; // TODO: must be service here

    public List<MyClassGeneralInfoDTO> getAllClasses() {
        List<Class> classes = classRepository.findAll();

        return classes.stream().map(
                cl -> new MyClassGeneralInfoDTO(
                        cl.getClassName(), cl.getClassCode(),
                        cl.getTeacher().getFullName()
                )
        ).collect(Collectors.toList());
    }

    public List<ClassMemberDTO> getClassMembers(String classCode) {
        Optional<Class> classOpt = classRepository.findByClassCode(classCode);

        if (classOpt.isPresent()) {
            List<User> members = classOpt.get().getMembers();

            return members.stream().map(
                    m -> new ClassMemberDTO(m.getUserId(), m.getFullName())
            ).collect(Collectors.toList());
        } else {
            throw new ApiException("Invalid class code!");
        }
    }

    public String createClass(String className) {
        String classCode = generateClassCode();

        User authenticatedUser = getAuthenticatedUser();

        Class newClass = new Class();

        newClass.setClassCode(classCode);
        newClass.setClassName(className);
        newClass.setTeacher(authenticatedUser);

        classRepository.save(newClass);

        return classCode;
    }

    private String generateClassCode() {
//        TODO: duplicates?
        int codeLength = 6;
        Random random = new Random();

        return random.ints('0', 'z' + 1)
                .filter(i -> (i <= '9' || i >= 'A') && (i <= 'Z' || i >= 'a'))
                .limit(codeLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public String joinClass(String classCode) {
        Optional<Class> classOpt = classRepository.findByClassCode(classCode);

        if (classOpt.isPresent()) {
            Class cl = classOpt.get();

            User authenticatedUser = getAuthenticatedUser();

            // TODO: teachers shouldn't be able to join their classes
            List<Class> enrolledClasses = authenticatedUser.getEnrolledClasses();
            enrolledClasses.add(cl);
            authenticatedUser.setEnrolledClasses(enrolledClasses);

            userRepository.save(authenticatedUser);

            return cl.getClassName();
        } else {
            throw new ApiException("Invalid class code!");
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        return userRepository.getById(userId);
    }
}
