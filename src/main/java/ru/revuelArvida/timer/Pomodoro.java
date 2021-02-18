package ru.revuelArvida.timer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.revuelArvida.PomodoroBot;

import javax.xml.bind.PropertyException;
import java.util.Timer;
import java.util.TimerTask;

@Component
@Scope("singleton")
public class Pomodoro {

    private PomodoroSettings pomodoroSettings;
    private SendMessage sendMessage;
    private TimerTask task;

    public void setPomodoroSettings(PomodoroSettings pomodoroSettings) {
        this.pomodoroSettings = pomodoroSettings;
    }

    public void setSendMessage(SendMessage sendMessage) {
        this.sendMessage = sendMessage;
    }

    private final PomodoroBot bot;
    private Timer timer;


    public Pomodoro(PomodoroBot bot){
        this.bot = bot;
    }

    public void startWork() throws PropertyException {
        if (pomodoroSettings == null || sendMessage == null){
            throw new PropertyException("Pomodoro properties or SendMessage not set");
        }


    }

    private void startWork(SendMessage sendMessage, PomodoroSettings settings) {
        if (settings.getCount() < 4){
        sendMessage.setText("Пора сделать перерыв! Отдохни " + settings.getCount() + " минут!");
        } if (settings.getCount() == 4){
            sendMessage.setText("Пора передохнуть! У тебя " + settings.getLongBrakePeriod() + " " +
                    "минут!");
        }

        timer = new Timer();
        PomodoroTask pomodoroTask = new PomodoroTask(bot, sendMessage);
        timer.schedule(pomodoroTask, settings.getWorkPeriod());
    }

    private void startShortBreak(SendMessage sendMessage,PomodoroSettings settings){
        timer = new Timer();
        timer.schedule(new PomodoroTask(bot, sendMessage),  settings.getShortBrakePeriod());
    }

    private void startLongBreak(SendMessage sendMessage, PomodoroSettings settings){
        timer = new Timer();
        PomodoroTask task = new PomodoroTask(bot, sendMessage);
        timer.schedule(task, settings.getLongBrakePeriod());
    }

    public void cancelTimer(){
        timer.cancel();
    }

}
