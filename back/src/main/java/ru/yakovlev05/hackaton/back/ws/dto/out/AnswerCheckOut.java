package ru.yakovlev05.hackaton.back.ws.dto.out;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerCheckOut extends BaseMessageOut{
    private MessageOut type = MessageOut.ANSWER_RESULT;

    private Long yourAnswerId; // Ответ, который дал пользователь
    private boolean isCorrect; // Правильный ли ответ

    private Long correctAnswerId; // Верный ответ

    private Long myHp;
    private Long hrHp;
}
