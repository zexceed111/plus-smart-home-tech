package ru.yandex.practicum.exception.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Value
@Builder
public class ErrorResponse {
    Instant timestamp;
    HttpStatus status;
    String error;
    String message;
    String path;

    @Builder.Default
    ErrorDetails details = null;

    @Value
    @Builder
    public static class ErrorDetails {
        String exception;
        String trace;
        String stackTrace;
    }
}