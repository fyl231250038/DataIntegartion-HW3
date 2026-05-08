package com.hw3.department.service;

import com.hw3.department.model.LoginRequest;
import com.hw3.department.model.LoginResponse;
import com.hw3.department.repository.UserRepository;
import com.hw3.department.security.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        boolean valid = userRepository.validate(request.getUsername(), request.getPassword());
        if (!valid) {
            return new LoginResponse(false, "invalid credentials", null, null);
        }
        String token = jwtService.generateToken(request.getUsername());
        return new LoginResponse(true, "login ok", token, request.getUsername());
    }
}
