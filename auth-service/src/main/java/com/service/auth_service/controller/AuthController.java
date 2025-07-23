package com.service.auth_service.controller;

import com.service.auth_service.dto.AuthResponse;
import com.service.auth_service.dto.LoginRequest;
import com.service.auth_service.dto.RegisterRequest;
import com.service.auth_service.entity.User;
import com.service.auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")  // Base path (API Gateway strips /api/auth)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            AuthResponse errorResponse = new AuthResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            AuthResponse errorResponse = new AuthResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestBody String token) {
        try {
            // Remove quotes if token is wrapped in quotes
            token = token.replace("\"", "");
            AuthResponse response = authService.validateToken(token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            AuthResponse errorResponse = new AuthResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    // Token Refresh Endpoint for Angular
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // Validate authorization header format
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                AuthResponse errorResponse = new AuthResponse("Invalid authorization header format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            // Extract token from header
            String oldToken = authHeader.substring(7); // Remove "Bearer " prefix
            // Extract user information from existing token
            String email = authService.getJwtService().extractEmail(oldToken);
            Long userId = authService.getJwtService().extractUserId(oldToken);
            String name = authService.getJwtService().extractName(oldToken);
            // Validate existing token
            if (!authService.getJwtService().isTokenValid(oldToken, email)) {
                AuthResponse errorResponse = new AuthResponse("Token expired or invalid");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            // Generate new token with fresh expiration
            String newToken = authService.getJwtService().generateToken(email, userId, name);
            AuthResponse response = new AuthResponse(newToken, email, name, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            AuthResponse errorResponse = new AuthResponse("Failed to refresh token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    // Get Current User Info Endpoint for Angular
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // Validate authorization header format
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                AuthResponse errorResponse = new AuthResponse("Invalid authorization header format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            // Extract token from header
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            // Extract user information from token
            String email = authService.getJwtService().extractEmail(token);
            Long userId = authService.getJwtService().extractUserId(token);
            String name = authService.getJwtService().extractName(token);
            // Validate token
            if (!authService.getJwtService().isTokenValid(token, email)) {
                AuthResponse errorResponse = new AuthResponse("Token expired or invalid");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            // Verify user still exists in database
            User user = authService.getUserByEmail(email);
            AuthResponse response = new AuthResponse(token, email, name, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            AuthResponse errorResponse = new AuthResponse("Failed to get current user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = authService.getUserById(id);
            // Don't return password in response
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AuthResponse(e.getMessage()));
        }
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            User user = authService.getUserByEmail(email);
            // Don't return password in response
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AuthResponse(e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is healthy! Total users: " + authService.getTotalUserCount());
    }

    @GetMapping("/info")
    public ResponseEntity<?> info() {
        return ResponseEntity.ok(new AuthResponse("Auth Service v1.0 - User management and JWT authentication"));
    }
}