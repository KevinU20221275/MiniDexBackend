package org.kmontano.minidex.controllers;

import jakarta.validation.Valid;
import org.kmontano.minidex.domain.trainer.DailyPackStatus;
import org.kmontano.minidex.dto.response.AuthResponse;
import org.kmontano.minidex.dto.response.TrainerDTO;
import org.kmontano.minidex.dto.request.AuthRequest;
import org.kmontano.minidex.dto.request.LoginRequest;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.application.serviceImpl.DailyPackServiceImpl;
import org.kmontano.minidex.application.service.TrainerService;
import org.kmontano.minidex.auth.JwtUtil;
import org.kmontano.minidex.utils.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controlador REST para autenticación.
 * Permite registrar usuarios y realizar login generando token JWT.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin("${frontend.url}")
public class AuthController {
    private final TrainerService trainerService;
    private final JwtUtil jwtUtil;
    private final DailyPackServiceImpl dailyPackServiceImpl;

    public AuthController(TrainerService trainerService, JwtUtil jwtUtil, DailyPackServiceImpl dailyPackServiceImpl) {
        this.trainerService = trainerService;
        this.jwtUtil = jwtUtil;
        this.dailyPackServiceImpl = dailyPackServiceImpl;
    }

    /**
     * Registro de un nuevo Trainer.
     * @param request DTO con nombre, username y password
     * @return AuthResponse con el token JWT y el TrainerDTO
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        // crea el nuevo entrenador y devulve el DTO
        TrainerDTO newTrainer = trainerService.create(request);

        // Generar el token
        String token = jwtUtil.generateToken(newTrainer.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, newTrainer));
    }

    /**
     * Login de un Trainer.
     * @param request DTO con username y password
     * @return Token JWT y TrainerDTO
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        // Obtiene el trainer con todos sus pokémon y tipos
        Trainer trainer = trainerService.findTrainerByUsername(request.getUsername())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
                );

        // Valida contraseña
        if (!PasswordEncoder.checkPassword(request.getPassword(), trainer.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // Genera token
        String token = jwtUtil.generateToken(trainer.getUsername());

        // resetea los packs si es necesario
        DailyPackStatus status = dailyPackServiceImpl.resetIfNeeded(trainer.getDailyPack());
        trainer.setDailyPack(status);

        // Convierte a DTO
        TrainerDTO trainerDTO = new TrainerDTO(trainer);

        return ResponseEntity.ok(new AuthResponse(token, trainerDTO));
    }
}


