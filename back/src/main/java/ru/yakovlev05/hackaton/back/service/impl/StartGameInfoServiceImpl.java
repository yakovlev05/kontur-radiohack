package ru.yakovlev05.hackaton.back.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yakovlev05.hackaton.back.dto.statistic.StartGameInfoResponseDto;
import ru.yakovlev05.hackaton.back.entity.StartGameInfo;
import ru.yakovlev05.hackaton.back.entity.inmemory.Game;
import ru.yakovlev05.hackaton.back.exception.NotFoundException;
import ru.yakovlev05.hackaton.back.mapper.StartGameInfoMapper;
import ru.yakovlev05.hackaton.back.repository.StartGameInfoRepository;
import ru.yakovlev05.hackaton.back.service.StartGameInfoService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StartGameInfoServiceImpl implements StartGameInfoService {

    private final StartGameInfoRepository startGameInfoRepository;
    private final StartGameInfoMapper startGameInfoMapper;

    @Override
    public List<StartGameInfoResponseDto> getStartsGamesList(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        List<StartGameInfo> starts = startGameInfoRepository.findAll(pageable).getContent();

        return starts.stream()
                .map(startGameInfoMapper::toDto)
                .toList();
    }

    @Override
    public void setIsWinByUsername(String username) {
        StartGameInfo startGameInfo = findByUsername(username);
        startGameInfo.setWin(true);
        startGameInfoRepository.save(startGameInfo);
    }

    @Override
    public void saveInfo(Game game, HttpServletRequest request) {
        StartGameInfo startGameInfo = new StartGameInfo();
        startGameInfo.setUsername(game.getUsername());
        startGameInfo.setIp(request.getRemoteAddr());
        startGameInfo.setUserAgent(request.getHeader("User-Agent"));

        save(startGameInfo);
    }

    private StartGameInfo findByUsername(String username) {
        return startGameInfoRepository.findLatestInfoByUsername(username)
                .orElseThrow(() -> new NotFoundException("Информация о старте игры с username '%s' не найдена", username));
    }

    private void save(StartGameInfo startGameInfo) {
        startGameInfoRepository.save(startGameInfo);
    }
}
