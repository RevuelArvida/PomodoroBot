package ru.revuelArvida.timer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.revuelArvida.PomodoroBot;

import java.util.TimerTask;


class PomodoroTask extends TimerTask {

    private PomodoroBot bot;
    private SendMessage sendMessage;


    PomodoroTask(PomodoroBot bot, SendMessage sendMessage){
        this.bot = bot;
        this.sendMessage = sendMessage;
    }

    @Override
    public void run() {
            bot.sendMessage(sendMessage);

    }
}
