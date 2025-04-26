package ru.yakovlev05.hackaton.back.service;

import ru.yakovlev05.hackaton.back.dto.session.CreateSessionRequestDto;
import ru.yakovlev05.hackaton.back.dto.session.CreateSessionResponseDto;

public interface SessionService {
    CreateSessionResponseDto createSession(CreateSessionRequestDto createSessionRequest);
}
