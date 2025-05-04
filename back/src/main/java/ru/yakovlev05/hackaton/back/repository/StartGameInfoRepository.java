package ru.yakovlev05.hackaton.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yakovlev05.hackaton.back.entity.StartGameInfo;

import java.util.Optional;

public interface StartGameInfoRepository extends JpaRepository<StartGameInfo, Long> {

    @Query(value = """
            select s from StartGameInfo s
            where s.username = :username
            order by s.createdAt desc
            limit 1
            """)
    Optional<StartGameInfo> findLatestInfoByUsername(String username);
}
