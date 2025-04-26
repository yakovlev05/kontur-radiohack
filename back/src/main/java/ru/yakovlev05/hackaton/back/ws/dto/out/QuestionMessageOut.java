package ru.yakovlev05.hackaton.back.ws.dto.out;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionMessageOut extends BaseMessageOut {
    private MessageOut type = MessageOut.QUESTION;
    private String text;
    private Long questionId;
    private List<Answer> answers;
    private Long secondsLeft;

    private Long myHp;
    private Long hrHp;

    @Getter
    @Setter
    public static class Answer {
        private Long id;
        private String text;
    }
}
