package ru.yakovlev05.hackaton.back.mapper;

import org.springframework.stereotype.Component;
import ru.yakovlev05.hackaton.back.dto.statistic.StartGameInfoResponseDto;
import ru.yakovlev05.hackaton.back.entity.StartGameInfo;

@Component
public class StartGameInfoMapper {

    public StartGameInfoResponseDto toDto(StartGameInfo entity) {
        return new StartGameInfoResponseDto(
                entity.getId(),
                entity.getUserAgent(),
                entity.getIp(),
                entity.getUsername(),
                entity.getCreatedAt(),
                entity.isWin()
        );
    }

}
