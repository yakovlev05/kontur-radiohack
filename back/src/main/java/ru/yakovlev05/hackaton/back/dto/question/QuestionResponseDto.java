package ru.yakovlev05.hackaton.back.dto.question;

import ru.yakovlev05.hackaton.back.dto.answer.AnswerResponseDto;

import java.util.List;

public record QuestionResponseDto(
        Long id,
        String text,
        List<AnswerResponseDto> answers
) {
}
