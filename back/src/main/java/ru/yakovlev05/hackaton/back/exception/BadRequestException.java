package ru.yakovlev05.hackaton.back.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String format, Object... args) {
        super(String.format(format, args));
    }
}
