package ru.yakovlev05.hackaton.back.mapper;

import org.springframework.stereotype.Component;
import ru.yakovlev05.hackaton.back.dto.statistic.ResultResponseDto;
import ru.yakovlev05.hackaton.back.entity.Result;

@Component
public class ResultMapper {

    public ResultResponseDto toDto(Result result) {
        return new ResultResponseDto(
                result.getId(),
                result.getUsername(),
                result.getScore(),
                result.getCompletedAt()
        );
    }
}
