package ru.revuelArvida.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

}
