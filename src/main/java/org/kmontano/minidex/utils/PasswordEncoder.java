package org.kmontano.minidex.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Clase helper para encriptar y para comprobar los passwords
 */
public class PasswordEncoder {
    private final static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    public static boolean checkPassword(String password, String hashedPassword){
        return passwordEncoder.matches(password, hashedPassword);
    }
}
