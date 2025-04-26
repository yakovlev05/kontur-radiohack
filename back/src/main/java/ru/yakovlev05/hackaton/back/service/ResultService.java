package ru.yakovlev05.hackaton.back.service;

import ru.yakovlev05.hackaton.back.dto.statistic.ResultResponseDto;
import ru.yakovlev05.hackaton.back.entity.Result;

import java.util.List;

public interface ResultService {
    List<ResultResponseDto> getTop(Integer page, Integer size);

    boolean existUsername(String username);

    void save(Result result);
}
