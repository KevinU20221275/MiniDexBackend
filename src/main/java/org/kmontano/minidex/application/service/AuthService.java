package org.kmontano.minidex.application.service;

import org.kmontano.minidex.dto.response.TrainerDTO;

public interface AuthService {
    TrainerDTO authenticate(String username, String rawPassword);
}
