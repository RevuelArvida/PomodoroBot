package ru.revuelArvida.timer;

import lombok.NoArgsConstructor;
import org.jvnet.hk2.annotations.Optional;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
@NoArgsConstructor
public class PomodoroSettings {

    private long workPeriod;
    private long shortBrakePeriod;
    private long longBrakePeriod;
    private int count;


    public long getWorkPeriod(Boolean inMillis) {
        if (inMillis == true){
            return workPeriod;
        } else return workPeriod / 60000L;
    }

    public void setWorkPeriod(int workPeriod) {
        this.workPeriod = workPeriod * 60000L;
    }

    public long getShortBrakePeriod( Boolean inMillis) {
        if (inMillis == true){
            return  shortBrakePeriod;
        } else return shortBrakePeriod / 60000L;
    }

    public void setShortBrakePeriod(int shortBrakePeriod) {
        this.shortBrakePeriod = shortBrakePeriod * 60000L;
    }

    public long getLongBrakePeriod(Boolean inMillis) {
        if (inMillis == true){
            return longBrakePeriod;
        } else return longBrakePeriod / 60000L;
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

    public void incrementCount() {count++;}

    public void decrementCount() {count--;}

}

