package ru.yandex.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import org.apache.catalina.connector.Request;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.model.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleNotAuthorizedUserException(Request request, NotAuthorizedException e) {
        return ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .error("Server error")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .details(ErrorResponse.ErrorDetails.builder()
                        .exception(e.getClass().getName())
                        .trace(ExceptionUtils.getStackTrace(e))
                        .build())
                .build();

    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(Request request, NotFoundException e) {
        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .error("Server error")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .details(ErrorResponse.ErrorDetails.builder()
                        .exception(e.getClass().getName())
                        .trace(ExceptionUtils.getStackTrace(e))
                        .build())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoProductsInShoppingCartException(Request request,
                                                                 NoProductsException e) {
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .error("Server error")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .details(ErrorResponse.ErrorDetails.builder()
                        .exception(e.getClass().getName())
                        .trace(ExceptionUtils.getStackTrace(e))
                        .build())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(Request request,
                                                            ConstraintViolationException e) {
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .error("Server error")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .details(ErrorResponse.ErrorDetails.builder()
                        .exception(e.getClass().getName())
                        .trace(ExceptionUtils.getStackTrace(e))
                        .build())
                .build();
    }
}
