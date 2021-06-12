package ru.ok.dwh.okdwhbot;

import chat.tamtam.bot.annotations.UpdateHandler;
import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.bot.longpolling.LongPollingBot;
import chat.tamtam.bot.longpolling.LongPollingBotOptions;
import chat.tamtam.botapi.TamTamBotAPI;
import chat.tamtam.botapi.exceptions.APIException;
import chat.tamtam.botapi.exceptions.ClientException;
import chat.tamtam.botapi.model.*;
import chat.tamtam.botapi.queries.SendMessageQuery;
import chat.tamtam.botapi.queries.TamTamQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ok.dwh.okdwhbot.Commands.CommandProcessor;
import ru.ok.dwh.okdwhbot.Sql.SqlProvider;
import ru.ok.dwh.okdwhbot.Sql.SqlMessage;
import ru.ok.dwh.okdwhbot.Tasks.SendStatus;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Timer;

public class DwhBot extends LongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    CommandProcessor commandProcessor;
    SqlProvider sqlProvider;
    TamTamBotAPI api;

    DwhBot(String accessToken, String url, String user, String password) {
        super(accessToken, LongPollingBotOptions.DEFAULT);
        this.sqlProvider = SqlProvider.getInstance(url, user, password);
        this.commandProcessor = new CommandProcessor(sqlProvider);
        this.api = TamTamBotAPI.create(accessToken);
        schedulerInit();
    }

    @UpdateHandler
    public void onMessageCreated(MessageCreatedUpdate update){
        Message sqlMessage = update.getMessage();
        Long chatId = sqlMessage.getRecipient().getChatId();
        sendMessageToChat(commandProcessor.processCommand(sqlMessage.getBody().getText()) ,chatId);
    }


    private void schedulerInit() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SendStatus(this), 0, 60 * 1000);
    }

    private void sendSafely(TamTamQuery<?> query) {
        try {
            query.execute();
        } catch (APIException | ClientException e) {
            LOG.error("Failed to execute query {}", query, e);
        }
    }

    public void sendMessageToChat(String message, Long chatId) {
        if (!message.isEmpty() && chatId!=null) {
            NewMessageBody messageBody = NewMessageBodyBuilder.ofText(message).build();
            SendMessageQuery query = new SendMessageQuery(getClient(), messageBody).chatId(chatId);
            sendSafely(query);
        }
    }

    public void sendMessageToUser(String message, Long userId) {
        if (!message.isEmpty() && userId!=null) {
            NewMessageBody messageBody = NewMessageBodyBuilder.ofText(message).build();
            SendMessageQuery query = new SendMessageQuery(getClient(), messageBody).userId(userId);
            sendSafely(query);
        }
    }

    @Override
    public void start() throws TamTamBotException {
        super.start();
        LOG.info("Bot started");
    }

    @Override
    public void stop() {
        super.stop();
        LOG.info("Bot stopped");
    }

    public boolean sendMessageToAllChats(String message) {
        try {
            List<Chat> chats = api.getChats().execute().getChats();
            for (Chat chat: chats) {
                if (chat.getType().equals(ChatType.CHAT)) {
                    sendMessageToChat(message, chat.getChatId());
                }
            }
            return true;
        } catch (APIException | ClientException e) {
            LOG.error(e.getMessage());
        }
        return false;
    }

    public void sendTasksStatus() {
        List<SqlMessage> sqlMessages = sqlProvider.getUnprocessedMessages("status");
        for (SqlMessage sqlMessage : sqlMessages) {
            if (sendMessageToAllChats(sqlMessage.getText())) {
                sqlProvider.markMessageAsProcessed(sqlMessage);
            }
        }
    }
}