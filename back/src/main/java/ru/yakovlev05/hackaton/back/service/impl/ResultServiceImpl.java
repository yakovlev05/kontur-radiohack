package ru.yakovlev05.hackaton.back.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yakovlev05.hackaton.back.dto.statistic.ResultResponseDto;
import ru.yakovlev05.hackaton.back.entity.Result;
import ru.yakovlev05.hackaton.back.mapper.ResultMapper;
import ru.yakovlev05.hackaton.back.repository.ResultRepository;
import ru.yakovlev05.hackaton.back.service.ResultService;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;
    private final ResultMapper resultMapper;

    @Override
    public List<ResultResponseDto> getTop(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "score");

        List<Result> results = resultRepository.findAll(pageable).getContent();

        return results.stream()
                .sorted(Comparator.comparing(Result::getScore).reversed())
                .map(resultMapper::toDto)
                .toList();
    }

    @Override
    public boolean existUsername(String username) {
        return resultRepository.existsByUsername(username);
    }

    @Override
    public void save(Result result) {
        resultRepository.save(result);
    }

}
