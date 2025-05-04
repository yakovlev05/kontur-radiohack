package ru.yakovlev05.hackaton.back.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yakovlev05.hackaton.back.dto.statistic.ResultResponseDto;
import ru.yakovlev05.hackaton.back.dto.statistic.StartGameInfoResponseDto;
import ru.yakovlev05.hackaton.back.service.ResultService;
import ru.yakovlev05.hackaton.back.service.StartGameInfoService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final ResultService resultService;
    private final StartGameInfoService startGameInfoService;

    @GetMapping
    public List<ResultResponseDto> getTop(@RequestParam(defaultValue = "0") Integer page,
                                          @RequestParam(defaultValue = "20") Integer size) {
        return resultService.getTop(page, size);
    }

    @SecurityRequirement(name = "AdminAuth")
    @GetMapping("/starts-games")
    public List<StartGameInfoResponseDto> getStartsGamesList(@RequestParam(defaultValue = "0") Integer page,
                                                             @RequestParam(defaultValue = "20") Integer size){
        return startGameInfoService.getStartsGamesList(page, size);
    }
}
