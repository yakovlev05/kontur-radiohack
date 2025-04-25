package ru.yakovlev05.hackaton.back.dto.question;

import ru.yakovlev05.hackaton.back.dto.answer.AnswerCreateRequestDto;

import java.util.List;

public record QuestionCreateRequestDto(
        String text,
        List<AnswerCreateRequestDto> answers
) {
}
