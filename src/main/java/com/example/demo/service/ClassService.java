package com.example.demo.service;

import com.example.demo.model.enums.Errors;
import com.example.demo.utils.Utils;
import com.example.demo.model.dto.exception.ApiException;
import com.example.demo.model.dto.response.ClassMemberDTO;
import com.example.demo.model.dto.response.MyClassGeneralInfoDTO;
import com.example.demo.model.entity.Class;
import com.example.demo.model.entity.User;
import com.example.demo.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private UserService userService;

    public List<MyClassGeneralInfoDTO> getAllClasses() {
        User user = userService.getAuthenticatedUser();

        List<Class> classes = classRepository.findAllByTeacher(user);

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
            throw new ApiException(Errors.INVALID_CLASS_CODE.getValue());
        }
    }

    public String createClass(String className) {
        String classCode = generateClassCode();

        User authenticatedUser = userService.getAuthenticatedUser();

        Class newClass = new Class(classCode, className, authenticatedUser);

        classRepository.save(newClass);

        return classCode;
    }

    private String generateClassCode() {
        String code;

        do {
            code = Utils.generateRandomCode(6);
        } while (classRepository.existsByClassCode(code));

        return code;
    }

    public String joinClass(String classCode) {
        Optional<Class> classOpt = classRepository.findByClassCode(classCode);

        if (classOpt.isPresent()) {
            Class cl = classOpt.get();

            User authenticatedUser = userService.getAuthenticatedUser();

            List<Class> enrolledClasses = authenticatedUser.getEnrolledClasses();
            enrolledClasses.add(cl);
            authenticatedUser.setEnrolledClasses(enrolledClasses);

            userService.saveUser(authenticatedUser);

            return cl.getClassName();
        } else {
            throw new ApiException(Errors.INVALID_CLASS_CODE.getValue());
        }
    }

}
