package com.hw3.department.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtService jwtService;
    private final AuthContext authContext;

    public JwtAuthFilter(JwtService jwtService, AuthContext authContext) {
        this.jwtService = jwtService;
        this.authContext = authContext;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/api/local/auth/login")) {
            return true;
        }
        if (path.startsWith("/api/external/integration")) {
            return true;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header for {} {}", request.getMethod(), request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("missing authorization");
            return;
        }
        String token = authHeader.substring("Bearer ".length());
        String studentId;
        try {
            studentId = jwtService.parseStudentId(token);
        } catch (Exception ex) {
            logger.warn("JWT parse failed: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("invalid token");
            return;
        }

        try {
            authContext.setStudentId(studentId);
            filterChain.doFilter(request, response);
        } finally {
            authContext.clear();
        }
    }
}
