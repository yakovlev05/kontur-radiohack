package ru.yakovlev05.hackaton.back.ws.dto.out;

public abstract class BaseMessageOut {
    public abstract MessageOut getType();

    public abstract Long getMyHp();

    public abstract Long getHrHp();
}
