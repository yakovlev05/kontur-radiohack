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
import ru.yakovlev05.hackaton.back.ws.dto.out.QuestionMessageOut;

import java.time.Duration;
import java.time.Instant;

/**
 * Отправляет вопрос на клик
 */
@RequiredArgsConstructor
@Component
public class SendQuestionHandler implements MessageHandler {

    private final GameService gameService;
    private final HelperService helperService;
    private final GameProps gameProps;
    private final QuestionService questionService;

    @Override
    public boolean canHandle(BaseMessageIn message, WebSocketSession session) {
        Game game = gameService.getById(helperService.extractGameId(session));
        int speed = gameService.calculateSpeed(game.getClicksAt());

        if (game.getMyAnswers().isEmpty()) {
            return speed >= gameProps.getRequireSpeedClicksQuestions()
                    && game.getStartedAt()!=null
                    && Duration.between(game.getStartedAt(), Instant.now()).getSeconds() > gameProps.getRequireTimeoutQuestionsInSeconds();
        }

        return speed >= gameProps.getRequireSpeedClicksQuestions()
                &&
                (
                        Duration.between(game.getMyAnswers().getLast().getExpiresAt(), Instant.now()).getSeconds() > gameProps.getRequireTimeoutQuestionsInSeconds()
                                || game.getMyAnswers().getLast().getIsCorrect() != null
                );
    }

    @Override
    public void handle(BaseMessageIn message, WebSocketSession session) {
        Game game = gameService.getById(helperService.extractGameId(session));
        gameService.incrementClicks(game);

        if (game.getHrHp() <= 0 || game.getMyHp() <= 0) { // Победа или поражение
            gameService.saveAndSendResultGameMessage(session, game);
            return;
        }

        Question question = questionService.getQuestionForGame(game);

        MyAnswer myAnswer = new MyAnswer();
        myAnswer.setQuestionId(question.getId());
        myAnswer.setSetAt(Instant.now());
        myAnswer.setExpiresAt(Instant.now().plusSeconds(gameProps.getTimeToAnswerInSeconds()));
        game.getMyAnswers().add(myAnswer);

        QuestionMessageOut questionMessageOut = helperService.toDto(question, myAnswer, game);

        helperService.serializeAndSend(session, questionMessageOut);
    }
}
