package ru.yakovlev05.hackaton.back.ws.handler.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import ru.yakovlev05.hackaton.back.entity.inmemory.Game;
import ru.yakovlev05.hackaton.back.service.GameService;
import ru.yakovlev05.hackaton.back.service.impl.HelperService;
import ru.yakovlev05.hackaton.back.ws.dto.in.BaseMessageIn;
import ru.yakovlev05.hackaton.back.ws.dto.out.SpeedMessageOut;

/**
 * Обработчик клика, возвращает скорость
 */
@RequiredArgsConstructor
@Component
public class ClickMessageHandler implements MessageHandler {

    private final HelperService helperService;
    private final GameService gameService;

    @Override
    public boolean canHandle(BaseMessageIn message, WebSocketSession session) {
        return true;
    }

    @Override
    public void handle(BaseMessageIn message, WebSocketSession session) {
        Game game = gameService.getById(helperService.extractGameId(session));

        gameService.incrementClicks(game);

        if (game.getHrHp() <= 0 || game.getMyHp() <= 0) { // Победа или поражение
            gameService.saveAndSendResultGameMessage(session, game);
            return;
        }

        int speed = gameService.calculateSpeed(game.getClicksAt());

        SpeedMessageOut speedMessageOut = helperService.toDto(speed, game);

        helperService.serializeAndSend(session, speedMessageOut);
    }
}
