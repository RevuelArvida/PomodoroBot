package ru.revuelArvida.task;


import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
public class TaskManager {

    Map<String, List<Task>> taskMap = new HashMap<>();
    private List<Task> taskList = new ArrayList<>();
    private int listSize;

    public void createTaskAtBegin(String text){
        taskList.add(0, new Task(text));
        listSize = taskList.size();
    }

    public void createTaskAtEnd(String text){
        taskList.add(new Task(text));
        listSize = taskList.size();
    }

    public void deleteTask(int index){

        if(index < listSize){
            throw new IndexOutOfBoundsException("Нет такой задачи");
        }

        taskList.remove(index - 1);
        listSize = taskList.size();
    }

    public void changeTask(int index, String text){
        if(index < listSize){
            throw new IndexOutOfBoundsException("Нет такой задачи");
        }

        taskList.remove(index - 1);
        taskList.add(index - 1, new Task(text));
    }

}
