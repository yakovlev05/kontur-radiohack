package ru.yakovlev05.hackaton.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yakovlev05.hackaton.back.dto.session.CreateGameRequestDto;
import ru.yakovlev05.hackaton.back.dto.session.CreateGameResponseDto;
import ru.yakovlev05.hackaton.back.service.GameService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/sessions")
public class GameController {

    private final GameService gameService;

    @PostMapping
    public CreateGameResponseDto createGame(@RequestBody CreateGameRequestDto createGameRequestDto) {
        return gameService.createGame(createGameRequestDto);
    }
}
