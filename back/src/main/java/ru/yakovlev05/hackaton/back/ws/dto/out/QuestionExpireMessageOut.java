package ru.yakovlev05.hackaton.back.ws.dto.out;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionExpireMessageOut extends BaseMessageOut {
    private MessageOut type = MessageOut.EXPIRE_ANSWER;
    private Long myHp;
    private Long hrHp;
}
