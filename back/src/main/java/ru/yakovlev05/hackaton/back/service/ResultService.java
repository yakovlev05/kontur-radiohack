package ru.yakovlev05.hackaton.back.service;

import ru.yakovlev05.hackaton.back.dto.statistic.ResultResponseDto;

import java.util.List;

public interface ResultService {
    List<ResultResponseDto> getTop(Integer page, Integer size);
}
