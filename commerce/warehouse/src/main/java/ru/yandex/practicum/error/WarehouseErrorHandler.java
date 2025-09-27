package ru.yandex.practicum.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.ApiError;
import ru.yandex.practicum.exception.DuplicateProductException;
import ru.yandex.practicum.exception.NotEnoughProductException;
import ru.yandex.practicum.exception.NotFoundException;

@RestControllerAdvice
public class WarehouseErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleDuplicateProductException(DuplicateProductException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, e, "Тип товара уже зарегистрирован на складе");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNotEnoughProductException(NotEnoughProductException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, e, "Недостаточно товаров на складе");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        return new ApiError(HttpStatus.NOT_FOUND, e, "Товар не найден на складе");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(Exception e) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e, "Неизвестная ошибка");
    }
}