package ru.yakovlev05.hackaton.back.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yakovlev05.hackaton.back.dto.exception.ExceptionResponseDto;
import ru.yakovlev05.hackaton.back.exception.BadRequestException;
import ru.yakovlev05.hackaton.back.exception.ConflictException;
import ru.yakovlev05.hackaton.back.exception.NotFoundException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponseDto handleException(Exception exception, HttpServletRequest request) {
        log.error("Internal Server Error", exception);
        return new ExceptionResponseDto(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponseDto handleNotFoundException(NotFoundException exception, HttpServletRequest request) {
        return new ExceptionResponseDto(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponseDto handleConflictException(ConflictException exception, HttpServletRequest request) {
        return new ExceptionResponseDto(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponseDto handleBadRequestException(BadRequestException exception, HttpServletRequest request) {
        return new ExceptionResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }
}
