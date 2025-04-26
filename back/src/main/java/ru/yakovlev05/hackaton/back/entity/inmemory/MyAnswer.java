package ru.yakovlev05.hackaton.back.entity.inmemory;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class MyAnswer {
    private Long questionId;
    private Long answerId;
    private Boolean isCorrect; // null - не ответил
    private Instant setAt; // Вопрос задан в ...
    private Instant expiresAt; // Истекает в ...
    private Instant answeredAt;
}
