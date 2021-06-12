package ru.ok.dwh.okdwhbot.Tasks;

import ru.ok.dwh.okdwhbot.DwhBot;

import java.util.TimerTask;

public class SendStatus extends TimerTask {

    DwhBot dwhBot;

    public SendStatus(DwhBot dwhBot) {
        this.dwhBot = dwhBot;
    }

    @Override
    public void run() {
        dwhBot.sendTasksStatus();
    }
}
