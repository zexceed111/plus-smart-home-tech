package ru.yandex.practicum.exception;

public class DeserializationException extends RuntimeException {
        public DeserializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }