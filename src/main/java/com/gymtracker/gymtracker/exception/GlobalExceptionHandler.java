package com.gymtracker.gymtracker.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Throwable cause = ex.getCause();
        if (cause != null && cause.getClass().getSimpleName().equals("InvalidFormatException")) {
            try {
                Class<?> targetType = (Class<?>) cause.getClass().getMethod("getTargetType").invoke(cause);
                if (targetType != null && targetType.isEnum()) {
                    String validValues = Arrays.stream(targetType.getEnumConstants())
                            .map(Object::toString)
                            .collect(Collectors.joining(", "));
                    return ResponseEntity.badRequest().body(
                            Map.of("error", "Invalid value. Allowed values: " + validValues));
                }
                Object value = cause.getClass().getMethod("getValue").invoke(cause);
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Invalid value: '" + value + "' for type " + (targetType != null ? targetType.getSimpleName() : "unknown")));
            } catch (Exception ignored) {}
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid request body"));
    }
}