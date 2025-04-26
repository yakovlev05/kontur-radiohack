package ru.yakovlev05.hackaton.back.ws.session;

import org.springframework.stereotype.Component;
import ru.yakovlev05.hackaton.back.entity.inmemory.Game;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameStorage {

    private final Map<String, Game> games = new ConcurrentHashMap<>(); // id-->game

    public Game getGame(String gameID) {
        return games.get(gameID);
    }

    public Collection<Game> getGames() {
        return games.values();
    }

    public void addGame(String id, Game game) {
        games.put(id, game);
    }

    public void removeGame(String id){
        games.remove(id);
    }

}
