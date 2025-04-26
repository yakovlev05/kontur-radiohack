package ru.yakovlev05.hackaton.back.ws.dto.out;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpeedMessageOut extends BaseMessageOut {
    private MessageOut type = MessageOut.SPEED;

    private int speed; // Кликов в секунду

    private Long myHp;
    private Long hrHp;
}
