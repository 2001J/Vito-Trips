package com.vitotrips.controller;

import com.vitotrips.DTO.RegistrationRequest;
import com.vitotrips.model.User;
import com.vitotrips.security.jwt.JwtProvider;
import com.vitotrips.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
                          JwtProvider jwtProvider,
                          AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        try {
            // Check if user already exists
            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body(Collections.singletonMap("error", "Email already registered"));
            }

            // Create new user using builder
            LocalDateTime now = LocalDateTime.now();
            User user = User.builder()
                    .email(request.getEmail())
                    .name(request.getName())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            // Save user
            User savedUser = userService.saveUser(user);

            Map<String, Object> response = createAuthResponse(savedUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Registration failed: " + e.getMessage()));
        }
    }

    private Map<String, Object> createAuthResponse(User savedUser) {
        // Generate token
        String token = jwtProvider.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        // Create response
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", savedUser.getEmail());
        response.put("role", savedUser.getRole().name());
        response.put("name", savedUser.getName());
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        try {
            // Use AuthenticationManager to validate credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            User user = userService.getUserByEmail(email);
            Map<String, Object> response = createAuthResponse(user);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An error occurred during authentication"));
        }
    }
}