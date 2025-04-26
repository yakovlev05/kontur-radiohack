package ru.yakovlev05.hackaton.back.service;

import org.springframework.web.socket.WebSocketSession;
import ru.yakovlev05.hackaton.back.dto.session.CreateGameRequestDto;
import ru.yakovlev05.hackaton.back.dto.session.CreateGameResponseDto;
import ru.yakovlev05.hackaton.back.entity.inmemory.Game;

import java.time.Instant;
import java.util.Deque;

public interface GameService {
    CreateGameResponseDto createGame(CreateGameRequestDto createGameRequestDto);

    boolean existGameId(String gameId);

    Game getById(String gameId);

    int calculateSpeed(Deque<Instant> clicks);

    void incrementClicks(Game game);

    void saveAndSendResultGameMessage(WebSocketSession session, Game game);

    void removeById(String id);
}
