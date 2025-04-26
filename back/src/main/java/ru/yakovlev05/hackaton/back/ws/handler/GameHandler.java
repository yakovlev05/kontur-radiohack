package ru.yakovlev05.hackaton.back.ws.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.yakovlev05.hackaton.back.ws.dto.WsDtoConverter;
import ru.yakovlev05.hackaton.back.ws.dto.in.BaseMessageIn;
import ru.yakovlev05.hackaton.back.ws.handler.message.MessageRegistry;

@Slf4j
@RequiredArgsConstructor
@Component
public class GameHandler extends TextWebSocketHandler {

    private final MessageRegistry messageRegistry;
    private final WsDtoConverter wsDtoConverter;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        BaseMessageIn converted = wsDtoConverter.convert(message.getPayload());

        messageRegistry.handle(session, converted);
    }

}
