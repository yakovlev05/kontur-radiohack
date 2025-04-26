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
    private Long durationOfGameInSeconds;
    private Long windowClicksSpeedInSeconds;
    private Long clickDamage;
    private Long requireTimeoutQuestionsInSeconds;
    private Long requireSpeedClicksQuestions;
    private Long timeToAnswerInSeconds;
    private Long damageRightAnswer;
    private Long damageWrongAnswer;
}
