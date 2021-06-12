package ru.ok.dwh.okdwhbot.Commands;

import ru.ok.dwh.okdwhbot.Sql.SqlProvider;

import java.util.Locale;

public class CommandProcessor {
    private final SqlProvider sqlProvider;

    public CommandProcessor(SqlProvider sqlProvider) {
        this.sqlProvider = sqlProvider;
    }

    public String processCommand(String command) {
        command = command.toLowerCase(Locale.ROOT);
        if (command.startsWith("/")) {
            if (command.equals("/status")) {
                return new StatusCommand(sqlProvider).process(command);
            } else {
                return "Unknown command: " + command;
            }
        }
        return null;
    }
}
