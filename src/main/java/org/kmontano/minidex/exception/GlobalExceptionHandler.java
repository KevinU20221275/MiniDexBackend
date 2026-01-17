package org.kmontano.minidex.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * Handles validation errors triggered by @Valid annotations
     * on request DTOs.
     *
     * Example response:
     * {
     *   "username": "Username must not be blank",
     *   "password": "Password must be at least 6 characters"
     * }
     *
     * HTTP Status: 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles domain-level validation errors.
     * These exceptions represent invalid business rules
     * or invalid domain state caused by client input.
     *
     * Example:
     * - Missing required trainer fields
     * - Invalid values (negative coins, level < 1)
     *
     * HTTP Status: 400 Bad Request
     */
    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<Map<String, String>> handleDomainValidation(
            DomainValidationException ex) {

        return ResponseEntity
                .badRequest()
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Handles domain conflicts.
     * These occur when the current state of a resource
     * does not allow the requested operation.
     *
     * Examples:
     * - Username already exists
     * - Daily envelope limit already reached
     *
     * HTTP Status: 409 Conflict
     */
    @ExceptionHandler(DomainConflictException.class)
    public ResponseEntity<Map<String, String>> handleDomainConflict(
            DomainConflictException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Handles cases where a requested resource
     * does not exist.
     *
     * Examples:
     * - Trainer not found
     * - Pokedex not found
     *
     * HTTP Status: 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(
            ResourceNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Handles ResponseStatusException thrown explicitly
     * from controllers or services.
     * Keeps a consistent error response format.
     *
     * HTTP Status: depends on the exception
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(
            ResponseStatusException ex) {

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of(
                        "message",
                        ex.getReason() != null ? ex.getReason() : "Error"
                ));
    }

    /**
     * Handles the exception thrown when a trainer attempts to open or claim
     * more envelopes than the allowed daily limit.
     *
     * This exception represents a business rule violation rather than a missing resource.
     * The trainer and envelope system exist, but the operation cannot be completed due to
     * the current state of the trainer's daily envelope usage.
     *
     * HTTP Status: {@code 409 CONFLICT}
     *
     * Response body example:
     * <pre>
     * {
     *   "message": "Daily envelope limit already reached"
     * }
     * </pre>
     *
     * @param ex the {@link EnvelopeLimitReachedException} thrown by the domain layer
     * @return a {@link ResponseEntity} containing a descriptive error message
     */
    @ExceptionHandler(EnvelopeLimitReachedException.class)
    public ResponseEntity<Map<String, String>> handleEnvelopeLimitReached(EnvelopeLimitReachedException ex){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "message",
                        ex.getMessage() != null ? ex.getMessage() : "Daily envelope limit already reached"
                ));
    }

    /**
     * Fallback handler for unexpected or unhandled errors.
     * These usually represent bugs or system failures.
     *
     * HTTP Status: 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Internal server error"));
    }
}
