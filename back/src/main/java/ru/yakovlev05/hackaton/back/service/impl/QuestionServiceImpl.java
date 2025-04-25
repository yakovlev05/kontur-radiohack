package ru.yakovlev05.hackaton.back.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yakovlev05.hackaton.back.dto.question.QuestionCreateRequestDto;
import ru.yakovlev05.hackaton.back.dto.question.QuestionResponseDto;
import ru.yakovlev05.hackaton.back.dto.question.QuestionUpdateRequestDto;
import ru.yakovlev05.hackaton.back.entity.Answer;
import ru.yakovlev05.hackaton.back.entity.Question;
import ru.yakovlev05.hackaton.back.exception.BadRequestException;
import ru.yakovlev05.hackaton.back.exception.NotFoundException;
import ru.yakovlev05.hackaton.back.mapper.QuestionMapper;
import ru.yakovlev05.hackaton.back.repository.QuestionRepository;
import ru.yakovlev05.hackaton.back.service.QuestionService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    @Override
    public QuestionResponseDto createQuestion(QuestionCreateRequestDto questionCreateRequest) {
        Question question = questionMapper.toEntity(questionCreateRequest);

        if (!validateQuestion(question)) {
            throw new BadRequestException("Вопрос должен иметь один верный ответ");
        }

        question.getAnswers()
                .forEach(answer -> answer.setQuestion(question));

        save(question);

        return questionMapper.toDto(question);
    }

    @Override
    public QuestionResponseDto getQuestionById(Long id) {
        Question question = findById(id);
        return questionMapper.toDto(question);
    }

    @Override
    public List<QuestionResponseDto> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(questionMapper::toDto)
                .toList();
    }

    @Override
    public void deleteQuestionById(Long id) {
        Question question = findById(id);
        questionRepository.delete(question);
    }

    @Override
    public QuestionResponseDto updateQuestionById(QuestionUpdateRequestDto questionUpdateRequest, Long id) {
        Question question = findById(id);
        save(questionMapper.updateEntity(questionUpdateRequest, question));

        return questionMapper.toDto(question);
    }

    private void save(Question question) {
        questionRepository.save(question);
    }

    private Question findById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вопрос с id '%d' не найден", id));
    }

    private boolean validateQuestion(Question question) {
        return question.getAnswers().stream()
                .filter(Answer::isCorrect)
                .count() == 1;
    }
}
