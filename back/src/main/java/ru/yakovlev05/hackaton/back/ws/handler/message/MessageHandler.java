package ru.yakovlev05.hackaton.back.ws.handler.message;

import org.springframework.web.socket.WebSocketSession;
import ru.yakovlev05.hackaton.back.ws.dto.in.BaseMessageIn;

public interface MessageHandler {
    boolean canHandle(BaseMessageIn message, WebSocketSession session);

    void handle(BaseMessageIn message, WebSocketSession session);
}
