package org.kmontano.minidex.controllers;

import jakarta.validation.Valid;
import org.kmontano.minidex.application.service.AuthService;
import org.kmontano.minidex.dto.response.AuthResponse;
import org.kmontano.minidex.dto.response.TrainerDTO;
import org.kmontano.minidex.dto.request.AuthRequest;
import org.kmontano.minidex.dto.request.LoginRequest;
import org.kmontano.minidex.application.service.TrainerService;
import org.kmontano.minidex.auth.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for authentication operations.
 *
 * Provides endpoints for trainer registration and login,
 * generating JWT tokens upon successful authentication.
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("${frontend.url}")
public class AuthController {
    private final TrainerService trainerService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public AuthController(TrainerService trainerService, JwtUtil jwtUtil, AuthService authService) {
        this.trainerService = trainerService;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    /**
     * Registers a new trainer.
     *
     * @param request DTO containing name, username and password
     * @return AuthResponse with JWT token and TrainerDTO
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        TrainerDTO newTrainer = trainerService.create(request);

        String token = jwtUtil.generateToken(newTrainer.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, newTrainer));
    }

    /**
     * Authenticates an existing trainer.
     *
     * @param request DTO containing username and password
     * @return AuthResponse with JWT token and TrainerDTO
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        TrainerDTO trainer = authService.authenticate(request.getUsername(), request.getPassword());

        String token = jwtUtil.generateToken(trainer.getUsername());

        return ResponseEntity.ok(new AuthResponse(token, trainer));
    }
}


