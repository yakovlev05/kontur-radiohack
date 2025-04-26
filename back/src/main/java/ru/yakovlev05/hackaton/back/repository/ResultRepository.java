package ru.yakovlev05.hackaton.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yakovlev05.hackaton.back.entity.Result;

public interface ResultRepository extends JpaRepository<Result, Long> {
}
