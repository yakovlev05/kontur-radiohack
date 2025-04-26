package ru.yakovlev05.hackaton.back.ws.dto.out;

import lombok.Getter;
import lombok.Setter;

// Время вышло
@Getter
@Setter
public class TimeUpMessageOut extends BaseMessageOut {
    private MessageOut type = MessageOut.TIME_UP;

    private boolean isWin;

    private Long myHp;
    private Long hrHp;
}
