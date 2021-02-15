package ru.revuelArvida.timer;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.revuelArvida.PomodoroBot;
import ru.revuelArvida.task.Task;

import java.util.Timer;

public class Pomodoro  {

    @Setter
    @Getter
    private int workPeriod;
    @Setter
    @Getter
    private int shortBrakePeriod;
    @Setter
    @Getter
    private int longBrakePeriod;
    private final PomodoroBot bot;
    private Timer timer;



    public Pomodoro(int workPeriod, int shortBrakePeriod, int longBrakePeriod, PomodoroBot bot){
        this.workPeriod = workPeriod;
        this.shortBrakePeriod = shortBrakePeriod;
        this.longBrakePeriod = longBrakePeriod;
        this.bot = bot;
    }

    public void startWork(SendMessage sendMessage) throws TelegramApiException {
        timer = new Timer();
        PomodoroTask pomodoroTask = new PomodoroTask(bot, sendMessage);
        timer.schedule(pomodoroTask, 0, workPeriod);
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

