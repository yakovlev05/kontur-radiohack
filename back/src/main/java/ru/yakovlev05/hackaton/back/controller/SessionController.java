package ru.yakovlev05.hackaton.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yakovlev05.hackaton.back.dto.session.CreateSessionRequestDto;
import ru.yakovlev05.hackaton.back.dto.session.CreateSessionResponseDto;
import ru.yakovlev05.hackaton.back.service.SessionService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public CreateSessionResponseDto createSession(@RequestBody CreateSessionRequestDto createSessionRequest) {
        return sessionService.createSession(createSessionRequest);
    }
}
