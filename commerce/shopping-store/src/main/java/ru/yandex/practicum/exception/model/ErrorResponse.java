package ru.yandex.practicum.exception.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
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
