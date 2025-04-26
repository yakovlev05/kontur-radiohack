package ru.yakovlev05.hackaton.back.ws.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import ru.yakovlev05.hackaton.back.service.GameService;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class AuthInterceptor implements HandshakeInterceptor {

    private final GameService gameService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String gameId = resolveGameId(request);

        if (gameId == null) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        if (!gameService.existGameId(gameId)){
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        attributes.put("gameId", gameId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    private String resolveGameId(ServerHttpRequest request) {
        if (request.getURI().getQuery() == null) {
            return null;
        }
        String[] queryPairs = request.getURI().getQuery().split("&");

        for (String queryPair : queryPairs) {
            String[] pair = queryPair.split("=");

            if (pair.length == 2 && pair[0].equals("gameId")) {
                return pair[1];
            }
        }

        return null;
    }
}
