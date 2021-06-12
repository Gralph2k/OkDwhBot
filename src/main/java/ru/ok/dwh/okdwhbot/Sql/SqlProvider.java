package ru.ok.dwh.okdwhbot.Sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlProvider {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static String GET_UNPROCESSED_MESSAGES =
        "SELECT id, message, registered, type " +
            "  FROM DwhBotMessages " +
            "  WHERE not is_processed AND type='%s'";

    private final static String MARK_MESSAGE_PROCESSED =
        "UPDATE DwhBotMessages SET is_processed=1 WHERE id=%d";

    private String url;
    private String user;
    private String password;
    private static SqlProvider sqlProvider;

    private SqlProvider() {
    }

    public static SqlProvider getInstance(String url, String user, String password) {
        if (sqlProvider == null) {
            sqlProvider = new SqlProvider();
            sqlProvider.url = url;
            sqlProvider.user = user;
            sqlProvider.password = password;
        }
        return sqlProvider;
    }

    private ResultSet executeQuery(String query) {
        try (
            Connection con = DriverManager.getConnection(url, user, password);
            Statement st = con.createStatement()) {
            return st.executeQuery(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
            LOG.error(ex.getMessage());
        }
        return null;
    }

    public List<SqlMessage> getUnprocessedMessages(String messageType) {
        List<SqlMessage> sqlMessages = new ArrayList<>();
        try (ResultSet rs = executeQuery(String.format(GET_UNPROCESSED_MESSAGES, messageType))) {
            while (rs != null && rs.next()) {
                SqlMessage sqlMessage = new SqlMessage(
                    rs.getLong("id"),
                    rs.getString("message"),
                    rs.getTimestamp("registered"),
                    rs.getString("type"));
                sqlMessages.add(sqlMessage);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            LOG.error(ex.getMessage());
        }
        return sqlMessages;
    }

    public boolean markMessageAsProcessed(SqlMessage sqlMessage) {
        try (ResultSet rs = executeQuery(String.format(MARK_MESSAGE_PROCESSED, sqlMessage.getId()))) {
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            LOG.error(ex.getMessage());
        }
        return false;
    }

}
