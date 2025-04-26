package ru.yakovlev05.hackaton.back.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.yakovlev05.hackaton.back.entity.Answer;
import ru.yakovlev05.hackaton.back.entity.Question;
import ru.yakovlev05.hackaton.back.entity.inmemory.Game;
import ru.yakovlev05.hackaton.back.entity.inmemory.MyAnswer;
import ru.yakovlev05.hackaton.back.ws.dto.out.QuestionMessageOut;
import ru.yakovlev05.hackaton.back.ws.dto.out.SpeedMessageOut;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
public class HelperService {

    private final ObjectMapper objectMapper;

    public String extractGameId(WebSocketSession session) {
        return (String) session.getAttributes().get("gameId");
    }

    public void serializeAndSend(WebSocketSession session, Object message) {
        try {
            String text = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        try {
            String text = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(text));
        } catch (JsonProcessingException e) {
            log.error("Ошибка при сериализации объекта в строку (отправка ответа ws)");
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Ошибка при отправке ответа ws");
            throw new RuntimeException(e);
        }
    }

    public QuestionMessageOut.Answer toDto(Answer answer) {
        var answerDto = new QuestionMessageOut.Answer();
        answerDto.setText(answer.getText());
        answerDto.setId(answer.getId());

        return answerDto;
    }

    public QuestionMessageOut toDto(Question question, MyAnswer myAnswer, Game game) {
        QuestionMessageOut questionMessageOut = new QuestionMessageOut();
        questionMessageOut.setText(question.getText());
        questionMessageOut.setSecondsLeft(Duration.between(Instant.now(), myAnswer.getExpiresAt()).getSeconds());
        questionMessageOut.setAnswers(question.getAnswers().stream()
                .map(this::toDto)
                .toList());
        questionMessageOut.setQuestionId(question.getId());

        questionMessageOut.setHrHp(game.getHrHp());
        questionMessageOut.setMyHp(game.getMyHp());

        return questionMessageOut;
    }

    public SpeedMessageOut toDto(int speed, Game game) {
        SpeedMessageOut speedMessageOut = new SpeedMessageOut();
        speedMessageOut.setSpeed(speed);
        speedMessageOut.setMyHp(game.getMyHp());
        speedMessageOut.setHrHp(game.getHrHp());

        return speedMessageOut;
    }
}
