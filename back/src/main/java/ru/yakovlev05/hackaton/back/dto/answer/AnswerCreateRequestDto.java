package ru.yakovlev05.hackaton.back.dto.answer;

public record AnswerCreateRequestDto(
        String text,
        boolean isCorrect
) {
}
