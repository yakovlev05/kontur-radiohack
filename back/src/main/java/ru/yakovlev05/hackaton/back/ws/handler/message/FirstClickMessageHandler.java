package ru.yakovlev05.hackaton.back.ws.handler.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import ru.yakovlev05.hackaton.back.entity.inmemory.Game;
import ru.yakovlev05.hackaton.back.props.GameProps;
import ru.yakovlev05.hackaton.back.service.GameService;
import ru.yakovlev05.hackaton.back.service.impl.HelperService;
import ru.yakovlev05.hackaton.back.ws.dto.in.BaseMessageIn;
import ru.yakovlev05.hackaton.back.ws.dto.out.SpeedMessageOut;
import ru.yakovlev05.hackaton.back.ws.dto.out.TimeUpMessageOut;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Первый клик, запускает игру
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class FirstClickMessageHandler implements MessageHandler {

    private final HelperService helperService;
    private final GameService gameService;
    private final ScheduledExecutorService scheduledExecutorService;
    private final GameProps gameProps;

    @Override
    public boolean canHandle(BaseMessageIn message, WebSocketSession session) {
        Game game = gameService.getById(helperService.extractGameId(session));
        return game.getStartedAt() == null;
    }

    @Override
    public void handle(BaseMessageIn message, WebSocketSession session) {
        Game game = gameService.getById(helperService.extractGameId(session));

        game.setStartedAt(Instant.now());

        scheduledExecutorService.schedule(wrapEndGame(session, game), gameProps.getDurationOfGameInSeconds(), TimeUnit.SECONDS);

        gameService.incrementClicks(game);

        if (game.getHrHp() <= 0 || game.getMyHp() <= 0) { // Победа или поражение
            gameService.saveAndSendResultGameMessage(session, game);
            return;
        }

        int speed = gameService.calculateSpeed(game.getClicksAt());

        SpeedMessageOut speedMessageOut = helperService.toDto(speed, game);

        helperService.serializeAndSend(session, speedMessageOut);
    }

    private Runnable wrapEndGame(WebSocketSession session, Game game) {
        return () -> endGame(session, game);
    }

    private void endGame(WebSocketSession session, Game game) {
        TimeUpMessageOut timeUpMessageOut = new TimeUpMessageOut();
        timeUpMessageOut.setMyHp(game.getMyHp());
        timeUpMessageOut.setHrHp(game.getHrHp());

        if (game.getHrHp() < 0) {
            timeUpMessageOut.setWin(true);
        }

        helperService.serializeAndSend(session, timeUpMessageOut);
        gameService.removeById(game.getId());
        try {
            session.close();
        } catch (IOException e) {
            log.error("Ошибка при закрытии ws соединения, когда игра закончена");
//            throw new RuntimeException(e);
        }
    }
}
