package ru.yakovlev05.hackaton.back.ws.dto.in;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClickMessageIn extends BaseMessageIn {
    private MessageIn type = MessageIn.CLICK;
}
