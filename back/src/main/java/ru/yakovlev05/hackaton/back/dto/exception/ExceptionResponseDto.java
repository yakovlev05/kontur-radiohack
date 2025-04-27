package ru.yakovlev05.hackaton.back.dto.exception;

import java.time.Instant;

public record ExceptionResponseDto(
        Instant timestamp,
        Integer status,
        String error,
        String path
) {
}
