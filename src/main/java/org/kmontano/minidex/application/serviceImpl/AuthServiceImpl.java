package org.kmontano.minidex.application.serviceImpl;

import org.kmontano.minidex.application.service.AuthService;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.response.TrainerDTO;
import org.kmontano.minidex.exception.DomainValidationException;
import org.kmontano.minidex.infrastructure.repository.TrainerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Application service responsible for authentication logic.
 *
 * Handles trainer authentication by validating credentials
 * and returning a safe TrainerDTO when authentication succeeds.
 *
 * This service communicates directly with the repository and
 * does not depend on other domain services.
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(TrainerRepository trainerRepository, PasswordEncoder passwordEncoder) {
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates a trainer using username and raw password.
     *
     * @param username the trainer username
     * @param rawPassword the plain text password provided by the client
     * @return TrainerDTO with authenticated trainer data
     * @throws DomainValidationException if credentials are invalid
     */
    @Override
    public TrainerDTO authenticate(String username, String rawPassword) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new DomainValidationException("Invalid credentials"));

        if (!trainer.matchesPassword(rawPassword, passwordEncoder)){
            throw new DomainValidationException("Invalid credentials");
        }

        return new TrainerDTO(trainer);
    }
}
