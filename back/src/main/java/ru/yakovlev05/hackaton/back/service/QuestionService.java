package ru.yakovlev05.hackaton.back.service;

import ru.yakovlev05.hackaton.back.dto.question.QuestionCreateRequestDto;
import ru.yakovlev05.hackaton.back.dto.question.QuestionResponseDto;
import ru.yakovlev05.hackaton.back.dto.question.QuestionUpdateRequestDto;
import ru.yakovlev05.hackaton.back.entity.Question;
import ru.yakovlev05.hackaton.back.entity.inmemory.Game;

import java.util.List;

public interface QuestionService {
    QuestionResponseDto createQuestion(QuestionCreateRequestDto questionCreateRequest);

    QuestionResponseDto getQuestionById(Long id);

    List<QuestionResponseDto> getAllQuestions();

    void deleteQuestionById(Long id);

    QuestionResponseDto updateQuestionById(QuestionUpdateRequestDto questionUpdateRequest, Long id);

    Question getQuestionForGame(Game game);

    Question findById(Long questionId);
}
