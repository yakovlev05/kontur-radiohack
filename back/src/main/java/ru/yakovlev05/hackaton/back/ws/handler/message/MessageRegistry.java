package ru.yakovlev05.hackaton.back.ws.handler.message;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import ru.yakovlev05.hackaton.back.ws.dto.in.BaseMessageIn;

import java.util.ArrayList;
import java.util.List;


@Component
public class MessageRegistry {
    private final List<MessageHandler> handlers = new ArrayList<>();

    public MessageRegistry(ClickMessageHandler clickMessageHandler,
                           FirstClickMessageHandler firstClickMessageHandler,
                           SendQuestionHandler sendQuestionHandler,
                           RetrySendQuestionHandler retrySendQuestionHandler,
                           AnswerMessageHandler answerMessageHandler) {
        handlers.add(firstClickMessageHandler);
        handlers.add(sendQuestionHandler);
        handlers.add(retrySendQuestionHandler);
        handlers.add(answerMessageHandler);
        handlers.add(clickMessageHandler);
    }

    public void handle(WebSocketSession session, BaseMessageIn message) {
        for (MessageHandler handler : handlers) {
            if (handler.canHandle(message, session)) {
                handler.handle(message, session);
                break;
            }
        }
    }
}
