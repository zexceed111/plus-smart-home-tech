package ru.yandex.practicum.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiError {
    private Throwable cause;
    private HttpStatus httpStatus;
    private String userMessage;
    private String message;

    public ApiError(HttpStatus httpStatus, Exception e, String userMessage) {
        this.cause = e.getCause();
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
        this.message = e.getMessage();
    }
}