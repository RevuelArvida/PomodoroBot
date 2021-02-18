package ru.revuelArvida.task;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Task {

    private String task;

    private Boolean isDone;
    private int pomodoroPeriods;

    public Task(String task){
        this.task = task;
        isDone = false;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public int getPomodoroPeriods() {
        return pomodoroPeriods;
    }

    public void setPomodoroPeriods(int pomodoroPeriods) {
        this.pomodoroPeriods = pomodoroPeriods;
    }

}
