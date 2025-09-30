package ru.yandex.practicum.exception.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    Throwable cause;
    StackTraceElement[] stackTrace;
    HttpStatus httpstatus;
    String userMessage;
    String message;
    Throwable[] suppressed;
    String localizedMessage;
}
