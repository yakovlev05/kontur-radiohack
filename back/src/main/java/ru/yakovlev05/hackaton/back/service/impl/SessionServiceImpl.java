package ru.yakovlev05.hackaton.back.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yakovlev05.hackaton.back.dto.session.CreateSessionRequestDto;
import ru.yakovlev05.hackaton.back.dto.session.CreateSessionResponseDto;
import ru.yakovlev05.hackaton.back.entity.inmemory.Session;
import ru.yakovlev05.hackaton.back.exception.ConflictException;
import ru.yakovlev05.hackaton.back.props.GameProps;
import ru.yakovlev05.hackaton.back.service.ResultService;
import ru.yakovlev05.hackaton.back.service.SessionService;
import ru.yakovlev05.hackaton.back.ws.session.SessionStorage;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SessionServiceImpl implements SessionService {

    private final SessionStorage sessionStorage;

    private final ResultService resultService;

    private final GameProps gameProps;

    @Override
    public CreateSessionResponseDto createSession(CreateSessionRequestDto createSessionRequest) {
        if (!validateUsername(createSessionRequest.username())) {
            throw new ConflictException("Имя пользователя '%s' уже занято", createSessionRequest.username());
        }

        String sessionId = UUID.randomUUID().toString();
        Session session = new Session();
        session.setMyHp(gameProps.getMyHp());
        session.setHrHp(gameProps.getHrHp());
        session.setUsername(createSessionRequest.username());

        sessionStorage.addSession(sessionId, session);

        return new CreateSessionResponseDto(sessionId);
    }

    private boolean validateUsername(String username) {
        if (resultService.existUsername(username)) {
            return false;
        }

        boolean isExistInSessions = sessionStorage.getSessions().stream()
                .anyMatch(session -> session.getUsername().equals(username));

        return !isExistInSessions;
    }
}
