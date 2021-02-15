package ru.revuelArvida.timer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.revuelArvida.PomodoroBot;

import java.util.Timer;

public class Pomodoro  {

    private int workPeriod;
    private int shortBrakePeriod;
    private int longBrakePeriod;
    private PomodoroBot bot;
    private Timer timer;



    public Pomodoro(int workPeriod, int shortBrakePeriod, int longBrakePeriod, PomodoroBot bot){
        this.workPeriod = workPeriod;
        this.shortBrakePeriod = shortBrakePeriod;
        this.longBrakePeriod = longBrakePeriod;
        this.bot = bot;
    }

    public void startWork(SendMessage sendMessage) throws TelegramApiException {
        timer = new Timer();
        PomodoroTask task = new PomodoroTask(bot, sendMessage);
        timer.schedule(task, 0, workPeriod);
    }

    public void startShortBreak(SendMessage sendMessage){
        timer = new Timer();
        PomodoroTask task = new PomodoroTask(bot, sendMessage);
        timer.schedule(task, 0, shortBrakePeriod);
    }

    public void startLongBreak(SendMessage sendMessage){
        timer = new Timer();
        PomodoroTask task = new PomodoroTask(bot, sendMessage);
        timer.schedule(task, 0, longBrakePeriod);
    }

    public void cancelTimer(){
        timer.cancel();
    }


}

