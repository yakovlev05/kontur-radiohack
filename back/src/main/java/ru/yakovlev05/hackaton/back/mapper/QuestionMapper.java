package ru.yakovlev05.hackaton.back.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yakovlev05.hackaton.back.dto.question.QuestionCreateRequestDto;
import ru.yakovlev05.hackaton.back.dto.question.QuestionResponseDto;
import ru.yakovlev05.hackaton.back.dto.question.QuestionUpdateRequestDto;
import ru.yakovlev05.hackaton.back.entity.Question;

@RequiredArgsConstructor
@Component
public class QuestionMapper {

    private final AnswerMapper answerMapper;

    public Question toEntity(QuestionCreateRequestDto dto) {
        Question question = new Question();
        question.setText(dto.text());
        question.setAnswers(dto.answers().stream()
                .map(answerMapper::toEntity)
                .toList());

        return question;
    }

    public QuestionResponseDto toDto(Question entity) {
        return new QuestionResponseDto(
                entity.getId(),
                entity.getText(),
                entity.getAnswers().stream()
                        .map(answerMapper::toDto)
                        .toList()
        );
    }

    public Question updateEntity(QuestionUpdateRequestDto dto, Question entity) {
        entity.setText(dto.text());
        return entity;
    }
}
