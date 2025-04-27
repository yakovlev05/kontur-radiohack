package ru.yakovlev05.hackaton.back.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import ru.yakovlev05.hackaton.back.ws.handler.GameHandler;
import ru.yakovlev05.hackaton.back.ws.interceptor.AuthInterceptor;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WsConfig implements WebSocketConfigurer {

    private final GameHandler gameHandler;

    private final AuthInterceptor authInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameHandler, "/ws/game")
                .setAllowedOrigins("*")
                .setAllowedOriginPatterns("/**")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .addInterceptors(authInterceptor); // Проверка сессии, валидация
    }
}
