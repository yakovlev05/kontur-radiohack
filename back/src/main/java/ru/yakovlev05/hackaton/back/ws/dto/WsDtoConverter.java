package ru.yakovlev05.hackaton.back.ws.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yakovlev05.hackaton.back.ws.dto.in.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WsDtoConverter {

    private final ObjectMapper objectMapper;

    private final Map<MessageIn, Class<?>> converters = new HashMap<>();

    public WsDtoConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        converters.put(MessageIn.CLICK, ClickMessageIn.class);
        converters.put(MessageIn.ANSWER, AnswerMessageIn.class);
    }

    public BaseMessageIn convert(String json) {
        MessageIn type = getTypeMessage(json);

        try {
            return (BaseMessageIn) objectMapper.readValue(json, converters.get(type));
        } catch (JsonProcessingException e) {
            log.error("Ошибка при десериализации входящего сообщения websocket (этап десериализации по полученному типу)");
            throw new RuntimeException(e);
        }

    }

    private MessageIn getTypeMessage(String json) {
        try {
            return objectMapper.readValue(json, WsMessageIn.class).getType();
        } catch (JsonProcessingException e) {
            log.error("Ошибка при десериализации входящего сообщения websocket (этап получения типа сообщения)");
            throw new RuntimeException(e);
        }
    }

}
