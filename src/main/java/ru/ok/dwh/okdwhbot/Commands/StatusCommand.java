package ru.ok.dwh.okdwhbot.Commands;

import ru.ok.dwh.okdwhbot.Sql.SqlProvider;
import ru.ok.dwh.okdwhbot.Sql.SqlMessage;

import java.util.List;

public class StatusCommand extends SqlCommand {

    public StatusCommand(SqlProvider sqlProvider) {
        super(sqlProvider);
    }

    public String process(String command) {
        List<SqlMessage> sqlMessages = sqlProvider.getUnprocessedMessages("status");
        StringBuilder text = new StringBuilder();
        for (SqlMessage msg: sqlMessages) {
            text.append(msg.getText()).append("\n");
        }
        return text.toString();
    }
}
