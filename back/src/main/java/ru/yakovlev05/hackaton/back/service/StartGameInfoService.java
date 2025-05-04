package ru.yakovlev05.hackaton.back.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.yakovlev05.hackaton.back.dto.statistic.StartGameInfoResponseDto;
import ru.yakovlev05.hackaton.back.entity.inmemory.Game;

import java.util.List;

public interface StartGameInfoService {
    List<StartGameInfoResponseDto> getStartsGamesList(Integer page, Integer size);

    void setIsWinByUsername(String username);

    void saveInfo(Game game, HttpServletRequest request);
}
