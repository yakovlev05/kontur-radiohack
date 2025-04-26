package ru.yakovlev05.hackaton.back.ws.handler.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import ru.yakovlev05.hackaton.back.entity.Answer;
import ru.yakovlev05.hackaton.back.entity.Question;
import ru.yakovlev05.hackaton.back.entity.inmemory.Game;
import ru.yakovlev05.hackaton.back.entity.inmemory.MyAnswer;
import ru.yakovlev05.hackaton.back.props.GameProps;
import ru.yakovlev05.hackaton.back.service.GameService;
import ru.yakovlev05.hackaton.back.service.QuestionService;
import ru.yakovlev05.hackaton.back.service.impl.HelperService;
import ru.yakovlev05.hackaton.back.ws.dto.in.AnswerMessageIn;
import ru.yakovlev05.hackaton.back.ws.dto.in.BaseMessageIn;
import ru.yakovlev05.hackaton.back.ws.dto.in.MessageIn;
import ru.yakovlev05.hackaton.back.ws.dto.out.AnswerCheckOut;

import java.time.Instant;

/**
 * Проверяет ответ на вопрос
 */
@RequiredArgsConstructor
@Component
public class AnswerMessageHandler implements MessageHandler {

    private final GameService gameService;
    private final HelperService helperService;
    private final QuestionService questionService;
    private final GameProps gameProps;

    @Override
    public boolean canHandle(BaseMessageIn message, WebSocketSession session) {
        Game game = gameService.getById(helperService.extractGameId(session));

        return message.getType().equals(MessageIn.ANSWER)
                && !game.getMyAnswers().isEmpty()
                && game.getMyAnswers().getLast().getIsCorrect() == null
                && game.getMyAnswers().getLast().getExpiresAt().isAfter(Instant.now());
    }

    @Override
    public void handle(BaseMessageIn message, WebSocketSession session) {
        Game game = gameService.getById(helperService.extractGameId(session));
        AnswerMessageIn answerMessageIn = (AnswerMessageIn) message;

        MyAnswer myAnswer = game.getMyAnswers().getLast();
        Question question = questionService.findById(myAnswer.getQuestionId());

        myAnswer.setAnswerId(answerMessageIn.getAnswerId());
        myAnswer.setIsCorrect(
                question.getAnswers().stream()
                        .filter(Answer::isCorrect)
                        .anyMatch(ans -> ans.getId().equals(answerMessageIn.getAnswerId()))
        );
        myAnswer.setAnsweredAt(Instant.now());

        if (myAnswer.getIsCorrect()) {
            game.setHrHp(game.getHrHp() - gameProps.getDamageRightAnswer());
        } else {
            game.setMyHp(game.getMyHp() - gameProps.getDamageWrongAnswer());
        }

        if (game.getHrHp() <= 0 || game.getMyHp() <= 0) { // Победа или поражение
            gameService.saveAndSendResultGameMessage(session, game);
            return;
        }

        AnswerCheckOut answerCheckOut = new AnswerCheckOut();
        answerCheckOut.setYourAnswerId(answerMessageIn.getAnswerId());
        answerCheckOut.setCorrectAnswerId(
                question.getAnswers().stream()
                        .filter(Answer::isCorrect)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Ошибка получения правильного ответа"))
                        .getId()
        );
        answerCheckOut.setCorrect(myAnswer.getIsCorrect());
        answerCheckOut.setHrHp(game.getHrHp());
        answerCheckOut.setMyHp(game.getMyHp());

        helperService.serializeAndSend(session, answerCheckOut);
    }
}
