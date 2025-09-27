package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class DeactivatedCartException extends RuntimeException {
    public DeactivatedCartException(String message) {
        super(message);
    }
}