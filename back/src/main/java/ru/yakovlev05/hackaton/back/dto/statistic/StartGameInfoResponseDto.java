package ru.yakovlev05.hackaton.back.dto.statistic;

import java.time.Instant;

public record StartGameInfoResponseDto(
        Long id,
        String userAgent,
        String ip,
        String username,
        Instant createdAt,
        boolean isWin
) {
}
