package ru.yakovlev05.hackaton.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yakovlev05.hackaton.back.dto.question.QuestionCreateRequestDto;
import ru.yakovlev05.hackaton.back.dto.question.QuestionResponseDto;
import ru.yakovlev05.hackaton.back.dto.question.QuestionUpdateRequestDto;
import ru.yakovlev05.hackaton.back.service.QuestionService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionResponseDto createQuestion(@RequestBody QuestionCreateRequestDto questionCreateRequest) {
        return questionService.createQuestion(questionCreateRequest);
    }

    @GetMapping("/{id}")
    public QuestionResponseDto getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    @GetMapping
    public List<QuestionResponseDto> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @PutMapping("/{id}")
    public QuestionResponseDto updateQuestionById(@RequestBody QuestionUpdateRequestDto questionUpdateRequest,
                                                  @PathVariable Long id) {
        return questionService.updateQuestionById(questionUpdateRequest, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestionById(@PathVariable Long id) {
        questionService.deleteQuestionById(id);
    }
}
