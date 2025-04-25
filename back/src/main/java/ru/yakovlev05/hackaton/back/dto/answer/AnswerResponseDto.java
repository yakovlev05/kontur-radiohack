package ru.yakovlev05.hackaton.back.dto.answer;

public record AnswerResponseDto(
        Long id,
        String text,
        boolean isCorrect
) {
}
