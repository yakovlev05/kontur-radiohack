package ru.yakovlev05.hackaton.back.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.List;
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
    public CreateGameResponseDto createGame(@RequestBody CreateGameRequestDto createGameRequestDto,
                                            HttpServletRequest request) {
        var headers = request.getHeaderNames();
        while (headers.hasMoreElements()){
            var header = headers.nextElement();
            System.out.printf("%s:%s%n", header, request.getHeader(header));
        }
        System.out.println("----------------------");
        System.out.println("----------------------");
        System.out.println("----------------------");
        return gameService.createGame(createGameRequestDto, request);
    }
}
