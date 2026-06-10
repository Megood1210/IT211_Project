package com.rikkeibank.exception;

import com.rikkeibank.dto.response.ApiResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<?> insufficient(InsufficientBalanceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.builder().status(409)
                .message(ex.getMessage()).data(null).build());
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<?> invalidPin(InvalidPinException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map
                .of("status", 400, "message", ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> notFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.builder().status(404)
                .message(ex.getMessage()).data(null).build());
    }

    @ExceptionHandler(InvalidResetTokenException.class)
    public ResponseEntity<?> invalidResetToken(InvalidResetTokenException ex) {
        return ResponseEntity.badRequest().body(Map.of("status", 400, "message",
                ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(),
                error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(ApiResponse.builder().status(400)
                .message("Validation failed").data(errors).build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtime(RuntimeException ex) {
        if (ex.getMessage().contains("Too many")) {
            return ResponseEntity.status(429).body(ApiResponse.builder().status(429)
                    .message(ex.getMessage()).data(null).build());
        }

        return ResponseEntity.internalServerError().body(ApiResponse.builder().status(500).message(ex.getMessage()).data(null).build());
    }
}