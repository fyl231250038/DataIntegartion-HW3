package com.hw3.department.security;

import org.springframework.stereotype.Component;

@Component
public class AuthContext {

    private final ThreadLocal<String> studentIdHolder = new ThreadLocal<>();

    public void setStudentId(String studentId) {
        studentIdHolder.set(studentId);
    }

    public String getStudentId() {
        return studentIdHolder.get();
    }

    public String requireStudentId() {
        String studentId = studentIdHolder.get();
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalStateException("unauthorized");
        }
        return studentId;
    }

    public void clear() {
        studentIdHolder.remove();
    }
}
