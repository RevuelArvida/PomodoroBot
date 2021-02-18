package ru.revuelArvida.timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.revuelArvida.PomodoroBot;

import javax.xml.bind.PropertyException;
import java.util.Timer;

@Component
@Scope("prototype")
public class Pomodoro {

    private PomodoroSettings pomodoroSettings;
    private SendMessage sendMessage;

    public PomodoroState getState() {
        return state;
    }

    private PomodoroState state;

    public void setPomodoroSettings(PomodoroSettings pomodoroSettings) {
        this.pomodoroSettings = pomodoroSettings;
    }

    public void setSendMessage(SendMessage sendMessage) {
        this.sendMessage = sendMessage;
    }

    private final PomodoroBot bot;
    private Timer timer;

    @Autowired
    public Pomodoro(PomodoroBot bot){
        state = PomodoroState.SLEEP;
        this.bot = bot;
    }




    public void startWork() throws IllegalArgumentException{

        if (pomodoroSettings == null || sendMessage == null){
            throw new IllegalArgumentException("Pomodoro properties or SendMessage not set");
        }

        state = PomodoroState.WORK;
        startWork(sendMessage, pomodoroSettings);
        pomodoroSettings.incrementCount();
    }


    public void startBreak() throws IllegalArgumentException {

        if (pomodoroSettings == null || sendMessage == null){
            throw new IllegalArgumentException("Pomodoro properties or SendMessage not set");
        }

        if(pomodoroSettings.getCount() == 4){
            state = PomodoroState.LONG_BRAKE;
            startLongBreak(sendMessage, pomodoroSettings);
            pomodoroSettings.setCount(0);
        } else {
            state = PomodoroState.SHORT_BRAKE;
            startShortBreak(sendMessage, pomodoroSettings);
        }
    }

    public void resume(){
        if (state == PomodoroState.WORK){
            startWork(sendMessage, pomodoroSettings);
        } else if (state == PomodoroState.SHORT_BRAKE){
            startShortBreak(sendMessage, pomodoroSettings);
        } else if (state == PomodoroState.LONG_BRAKE){
            startLongBreak(sendMessage, pomodoroSettings);
        } else
        {
            throw new IllegalStateException("Timer haven't start working");
        }
    }

    private void startWork(SendMessage sendMessage, PomodoroSettings settings) {
        if (settings.getCount() < 4){
        sendMessage.setText("Пора сделать перерыв! Отдохни " + settings.getShortBrakePeriod(false) +
                " минут!");
        } if (settings.getCount() == 4){
            sendMessage.setText("Пора передохнуть! У тебя " + settings.getLongBrakePeriod(false) + " " +
                    "минут!");
        }

        timer = new Timer();
        PomodoroTask pomodoroTask = new PomodoroTask(bot, sendMessage);
        timer.schedule(pomodoroTask, settings.getWorkPeriod(true));
    }

    private void startShortBreak(SendMessage sendMessage,PomodoroSettings settings){
        sendMessage.setText("Пора поработать!");
        timer = new Timer();
        timer.schedule(new PomodoroTask(bot, sendMessage),  settings.getShortBrakePeriod(true));
    }

    private void startLongBreak(SendMessage sendMessage, PomodoroSettings settings){
        timer = new Timer();
        sendMessage.setText("Пора поработать!");
        PomodoroTask task = new PomodoroTask(bot, sendMessage);
        timer.schedule(task, settings.getLongBrakePeriod(true));
    }

    public void cancelTimer(){
        timer.cancel();
        if (state == PomodoroState.WORK){
            pomodoroSettings.decrementCount();
        }

    }

}
