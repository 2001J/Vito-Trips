package com.vitotrips.controller;

import com.vitotrips.DTO.RegistrationRequest;
import com.vitotrips.model.User;
import com.vitotrips.security.jwt.JwtProvider;
import com.vitotrips.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final ReactiveAuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
                          JwtProvider jwtProvider,
                          ReactiveAuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<Map<String, Object>>> register(@RequestBody RegistrationRequest request) {
        return Mono.fromCallable(() -> {
            try {
                // Check if user already exists
                if (userService.existsByEmail(request.getEmail())) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Email already registered");
                    return ResponseEntity
                            .badRequest()
                            .body(errorResponse);
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
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Registration failed: " + e.getMessage());
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(errorResponse);
            }
        });
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
    public Mono<ResponseEntity<Map<String, Object>>> login(@RequestParam String email, @RequestParam String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        )
        .flatMap(authentication -> {
            try {
                User user = userService.getUserByEmail(email);
                Map<String, Object> response = createAuthResponse(user);
                return Mono.just(ResponseEntity.ok(response));
            } catch (Exception e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "An error occurred during authentication");
                return Mono.just(ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(errorResponse));
            }
        })
        .onErrorResume(AuthenticationException.class, e -> {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid credentials");
            return Mono.just(ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse));
        })
        .onErrorResume(e -> {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred during authentication");
            return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse));
        });
    }
}