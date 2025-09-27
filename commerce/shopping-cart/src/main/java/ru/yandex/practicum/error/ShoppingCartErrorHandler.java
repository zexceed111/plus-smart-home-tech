package ru.yandex.practicum.error;

import feign.RetryableException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.*;

@RestControllerAdvice
public class ShoppingCartErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleUnauthorizedUserException(UnauthorizedUserException e) {
        return new ApiError(HttpStatus.UNAUTHORIZED, e, "Пользователь не авторизован");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.LOCKED)
    public ApiError handleDeactivatedCartException(DeactivatedCartException e) {
        return new ApiError(HttpStatus.LOCKED, e, "Корзина деактивирована");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        return new ApiError(HttpStatus.NOT_FOUND, e, "Сущность не найдена");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiError handleRetryableException(RetryableException e) {
        return new ApiError(HttpStatus.SERVICE_UNAVAILABLE, e, "Сервис не доступен");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception e) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e, "Внутренняя ошибка сервера");
    }
}