package ru.ok.dwh.okdwhbot.Sql;

import java.sql.Timestamp;

public class SqlMessage {
    long id;
    String text;
    Timestamp registered;
    String type;

    public SqlMessage(long id, String text, Timestamp registered, String type) {
        this.id = id;
        this.text = text;
        this.registered = registered;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Timestamp getRegistered() {
        return registered;
    }

    public String getType() {
        return type;
    }
}
