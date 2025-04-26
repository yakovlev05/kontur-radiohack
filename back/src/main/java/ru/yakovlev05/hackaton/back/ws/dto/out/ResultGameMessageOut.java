package ru.yakovlev05.hackaton.back.ws.dto.out;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultGameMessageOut extends BaseMessageOut {
    private MessageOut type = MessageOut.RESULT;

    private boolean isWin;
    private Long score;
    private String username;

    private Long myHp;
    private Long hrHp;
}
