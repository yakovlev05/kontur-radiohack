package ru.yakovlev05.hackaton.back.dto.statistic;

import java.time.Instant;

public record ResultResponseDto(
        Long id,
        String username,
        Long score,
        Instant completedAt
) {
}
