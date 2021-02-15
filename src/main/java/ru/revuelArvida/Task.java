package ru.revuelArvida;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class Task {

    private String task;
    private Boolean isDone;

    public Task(String task){
        this.task = task;
        isDone = false;
    }

}
