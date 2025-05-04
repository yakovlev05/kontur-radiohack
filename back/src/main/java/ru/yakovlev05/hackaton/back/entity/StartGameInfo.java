package ru.yakovlev05.hackaton.back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "starts_games")
public class StartGameInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userAgent;

    private String ip;

    private String username;

    @CreationTimestamp
    private Instant createdAt;

    private boolean isWin;
}
