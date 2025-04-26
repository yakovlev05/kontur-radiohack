package ru.yakovlev05.hackaton.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yakovlev05.hackaton.back.entity.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = """
            SELECT * FROM questions
            WHERE questions.id NOT IN :ids
            ORDER BY random()
            LIMIT 1
            """, nativeQuery = true)
    Optional<Question> findRandomNotIdInList(@Param("ids") List<Long> ids);

    @Query(value = """
            SELECT * FROM questions
            ORDER BY random()
            LIMIT 1
            """, nativeQuery = true)
    Optional<Question> findRandom();
}
