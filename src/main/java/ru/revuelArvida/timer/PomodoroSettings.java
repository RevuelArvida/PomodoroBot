package ru.revuelArvida.timer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.revuelArvida.PomodoroBot;
import ru.revuelArvida.PomodoroBotApp;

import java.util.Timer;

@Component
@Scope("prototype")
@NoArgsConstructor
public class PomodoroSettings {

    private long workPeriod;
    private long shortBrakePeriod;
    private long longBrakePeriod;

    private int count;
    private boolean isLong;

    public long getWorkPeriod() {
        return workPeriod / 60000;
    }

    public void setWorkPeriod(int workPeriod) {
        this.workPeriod = workPeriod * 60000L;
    }

    public long getShortBrakePeriod() {
        return shortBrakePeriod / 60000L;
    }

    public void setShortBrakePeriod(int shortBrakePeriod) {
        this.shortBrakePeriod = shortBrakePeriod * 60000L;
    }

    public long getLongBrakePeriod() {
        return longBrakePeriod / 60000L;
    }

    public void setLongBrakePeriod(int longBrakePeriod) {
        this.longBrakePeriod =  (longBrakePeriod * 60000L);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isLong() {
        return isLong;
    }

    public void setLong(boolean aLong) {
        isLong = aLong;
    }
}

