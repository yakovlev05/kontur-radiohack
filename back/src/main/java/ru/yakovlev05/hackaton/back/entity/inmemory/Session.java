package ru.yakovlev05.hackaton.back.entity.inmemory;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
public class Session {

    private String id = UUID.randomUUID().toString();
    private Instant createdAt = Instant.now();
    private Instant startedAt;
    private Deque<Instant> clicksAt = new LinkedList<>(); // Для подсчета скорости кликов
    private Long countClicks = 0L;
    private Set<Long> questionsIds = new HashSet<>();
    private Long myHp;
    private Long hrHp;
    private Instant finishedAt;
    private GameResult gameResult = GameResult.NOT_FINISHED;
    private Long countQuestions = 0L;
    private Long countCorrectAnswers = 0L;
    private String username;


    public enum GameResult {
        WIN, LOSE, NOT_FINISHED
    }
}
