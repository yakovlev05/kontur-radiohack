package ru.yakovlev05.hackaton.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yakovlev05.hackaton.back.dto.statistic.ResultResponseDto;
import ru.yakovlev05.hackaton.back.service.ResultService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/statistics")
public class ResultController {

    private final ResultService resultService;

    @GetMapping
    public List<ResultResponseDto> getTop(@RequestParam(defaultValue = "0") Integer page,
                                          @RequestParam(defaultValue = "20") Integer size) {
        return resultService.getTop(page, size);
    }
}
