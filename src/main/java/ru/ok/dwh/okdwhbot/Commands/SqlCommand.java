package ru.ok.dwh.okdwhbot.Commands;

import ru.ok.dwh.okdwhbot.Sql.SqlProvider;

abstract class SqlCommand extends Command {
    SqlProvider sqlProvider;

    public SqlCommand(SqlProvider sqlProvider) {
        this.sqlProvider = sqlProvider;
    }

    @Override
    abstract public String process(String command);
}
