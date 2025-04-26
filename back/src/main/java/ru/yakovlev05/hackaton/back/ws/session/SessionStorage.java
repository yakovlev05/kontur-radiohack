package ru.yakovlev05.hackaton.back.ws.session;

import org.springframework.stereotype.Component;
import ru.yakovlev05.hackaton.back.entity.inmemory.Session;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionStorage {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>(); // id-->session

    public Session getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public Collection<Session> getSessions() {
        return sessions.values();
    }

    public void addSession(String id, Session session) {
        sessions.put(id, session);
    }

}
