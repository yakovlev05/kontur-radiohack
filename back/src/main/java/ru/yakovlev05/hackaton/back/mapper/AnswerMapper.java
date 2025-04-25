package ru.yakovlev05.hackaton.back.mapper;

import org.springframework.stereotype.Component;
import ru.yakovlev05.hackaton.back.dto.answer.AnswerCreateRequestDto;
import ru.yakovlev05.hackaton.back.dto.answer.AnswerResponseDto;
import ru.yakovlev05.hackaton.back.entity.Answer;

@Component
public class AnswerMapper {

    public Answer toEntity(AnswerCreateRequestDto dto) {
        Answer answer = new Answer();
        answer.setText(dto.text());
        answer.setCorrect(dto.isCorrect());
        return answer;
    }

    public AnswerResponseDto toDto(Answer entity) {
        return new AnswerResponseDto(
                entity.getId(),
                entity.getText(),
                entity.isCorrect()
        );
    }
}
