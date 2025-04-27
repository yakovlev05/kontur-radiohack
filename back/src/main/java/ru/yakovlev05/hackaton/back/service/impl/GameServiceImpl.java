package ru.yakovlev05.hackaton.back.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import ru.yakovlev05.hackaton.back.dto.session.CreateGameRequestDto;
import ru.yakovlev05.hackaton.back.dto.session.CreateGameResponseDto;
import ru.yakovlev05.hackaton.back.entity.Result;
import ru.yakovlev05.hackaton.back.entity.inmemory.Game;
import ru.yakovlev05.hackaton.back.entity.inmemory.MyAnswer;
import ru.yakovlev05.hackaton.back.exception.ConflictException;
import ru.yakovlev05.hackaton.back.exception.NotFoundException;
import ru.yakovlev05.hackaton.back.props.GameProps;
import ru.yakovlev05.hackaton.back.service.GameService;
import ru.yakovlev05.hackaton.back.service.ResultService;
import ru.yakovlev05.hackaton.back.ws.dto.out.ResultGameMessageOut;
import ru.yakovlev05.hackaton.back.ws.session.GameStorage;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Deque;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameServiceImpl implements GameService {

    private final GameStorage gameStorage;

    private final ResultService resultService;

    private final GameProps gameProps;
    private final HelperService helperService;

    @Override
    public CreateGameResponseDto createGame(CreateGameRequestDto createGameRequestDto) {
        if (!validateUsername(createGameRequestDto.username())) {
            throw new ConflictException("Имя пользователя '%s' уже занято", createGameRequestDto.username());
        }

        Game game = new Game();
        game.setMyHp(gameProps.getMyHp());
        game.setHrHp(gameProps.getHrHp());
        game.setUsername(createGameRequestDto.username());

        gameStorage.addGame(game.getId(), game);

        return new CreateGameResponseDto(game.getId());
    }

    @Override
    public boolean existGameId(String gameId) {
        return gameStorage.getGame(gameId) != null;
    }

    @Override
    public Game getById(String gameId) {
        Game game = gameStorage.getGame(gameId);
        if (game == null) {
            throw new NotFoundException("Игра с id '%s' не найдена", gameId);
        }
        return game;
    }

    @Override
    public int calculateSpeed(Deque<Instant> clicks) {
        Instant window = Instant.now().minus(gameProps.getWindowClicksSpeedInSeconds(), ChronoUnit.SECONDS);

        int count = 0;
        for (Instant click : clicks) {
            if (click.isAfter(window)) {
                count++;
            }
        }

        return count;
    }

    @Override
    public void incrementClicks(Game game) {
        game.setCountClicks(game.getCountClicks() + 1);
        game.getClicksAt().add(Instant.now());

        game.setHrHp(game.getHrHp() - gameProps.getClickDamage());
    }

    @Override
    public void saveAndSendResultGameMessage(WebSocketSession session, Game game) {
        boolean isWin = game.getHrHp() <= 0;
        Long score = calculateScore(game);
        if (isWin) {
            Result result = new Result();
            result.setUsername(game.getUsername());
            result.setScore(score);
            result.setCompletedAt(Instant.now());

            resultService.save(result);

        }

        gameStorage.removeGame(game.getId());

        ResultGameMessageOut resultGameMessageOut = toDto(game, score, isWin);
        helperService.serializeAndSend(session, resultGameMessageOut);
        if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log.error("Ошибка при закрытии ws после отправки результата");
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void removeById(String id) {
        gameStorage.removeGame(id);
    }

    private Long calculateScore(Game game) {
        long countSuccessAnswer = game.getMyAnswers().stream()
                .filter(MyAnswer::getIsCorrect)
                .count();
        return (countSuccessAnswer * 10 + game.getCountClicks() * 2) + (game.getMyHp() - game.getHrHp())
                + (1000 / Duration.between(game.getStartedAt(), game.getFinishedAt()).getSeconds());
    }

    private boolean validateUsername(String username) {
        if (resultService.existUsername(username)) {
            return false;
        }

        boolean isExistInSessions = gameStorage.getGames().stream()
                .anyMatch(game -> game.getUsername().equals(username));

        return !isExistInSessions;
    }

    public ResultGameMessageOut toDto(Game game, Long score, boolean isWin) {
        ResultGameMessageOut resultGameMessageOut = new ResultGameMessageOut();
        resultGameMessageOut.setWin(isWin);
        resultGameMessageOut.setScore(score);
        resultGameMessageOut.setUsername(game.getUsername());
        resultGameMessageOut.setHrHp(game.getHrHp());
        resultGameMessageOut.setMyHp(game.getMyHp());

        return resultGameMessageOut;
    }
}
