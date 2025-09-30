package ru.yandex.practicum.exception;

public class NoProductsException extends RuntimeException {
    public NoProductsException(String message) {
        super(message);
    }
}
