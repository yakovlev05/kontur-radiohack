package ru.yakovlev05.hackaton.back.ws.handler.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import ru.yakovlev05.hackaton.back.entity.Question;
import ru.yakovlev05.hackaton.back.entity.inmemory.Game;
import ru.yakovlev05.hackaton.back.entity.inmemory.MyAnswer;
import ru.yakovlev05.hackaton.back.props.GameProps;
import ru.yakovlev05.hackaton.back.service.GameService;
import ru.yakovlev05.hackaton.back.service.QuestionService;
import ru.yakovlev05.hackaton.back.service.impl.HelperService;
import ru.yakovlev05.hackaton.back.ws.dto.in.BaseMessageIn;
import ru.yakovlev05.hackaton.back.ws.dto.in.MessageIn;
import ru.yakovlev05.hackaton.back.ws.dto.out.QuestionMessageOut;

import java.time.Instant;

/**
 * Отправляет вопрос на клик. Отправляет, если есть текущий активный вопрос
 */
@RequiredArgsConstructor
@Component
public class RetrySendQuestionHandler implements MessageHandler {

    private final GameService gameService;
    private final HelperService helperService;
    private final GameProps gameProps;
    private final QuestionService questionService;

    @Override
    public boolean canHandle(BaseMessageIn message, WebSocketSession session) {
        Game game = gameService.getById(helperService.extractGameId(session));
        return !message.getType().equals(MessageIn.ANSWER)
                && !game.getMyAnswers().isEmpty()
                && game.getMyAnswers().getLast().getIsCorrect() == null
                && game.getMyAnswers().getLast().getExpiresAt().isAfter(Instant.now());
    }

    @Override
    public void handle(BaseMessageIn message, WebSocketSession session) {
        Game game = gameService.getById(helperService.extractGameId(session));

        MyAnswer myAnswer = game.getMyAnswers().getLast();
        Question question = questionService.findById(myAnswer.getQuestionId());


        QuestionMessageOut questionMessageOut = helperService.toDto(question, myAnswer, game);

        helperService.serializeAndSend(session, questionMessageOut);
    }
}
