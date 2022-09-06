package com.example.demo.model.enums;

public enum ERole {
    ROLE_STUDENT("ROLE_STUDENT"),
    ROLE_TEACHER("ROLE_TEACHER"),
    ROLE_ADMIN("ROLE_ADMIN");

    ERole(String role) {
    }

    public static ERole fromValue(String value) {
        switch (value) {
            case "STUDENT": return ROLE_STUDENT;
            case "TEACHER": return ROLE_TEACHER;
            case "ADMIN": return ROLE_ADMIN;
        }
        return null;
    }
}
