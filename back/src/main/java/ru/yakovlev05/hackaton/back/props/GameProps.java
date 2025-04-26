package ru.yakovlev05.hackaton.back.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "game")
public class GameProps {
    private Long myHp;
    private Long hrHp;
}
