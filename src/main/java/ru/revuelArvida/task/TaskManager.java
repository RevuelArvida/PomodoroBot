package ru.revuelArvida.task;


import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;

@Component
@Scope("singleton")
@NoArgsConstructor
public class TaskManager {

    Map<String, List<Task>> taskMap = new HashMap<>();

    public List<Task> getTaskList(Message message){
        List<Task> taskList = taskMap.get(message.getChatId().toString());

        if (taskList == null){
            taskList = new ArrayList<>();
            taskMap.put(message.getChatId().toString(), taskList);
        }
        return taskList;
    }

    public void createTaskAtBegin(Message message){
        List<Task> taskList = getTaskList(message);
        taskList.add(0, new Task(message.getText()));
    }

    public void createTaskAtEnd(Message message){
        List<Task> taskList = getTaskList(message);
        taskList.add(new Task(message.getText()));
    }

    public void createTaskAtIndex(Message message, int index){
        List<Task> taskList = getTaskList(message);
        taskList.add(index - 1, new Task(message.getText()));
    }

    public void deleteTask(Message message) throws IndexOutOfBoundsException, NumberFormatException{
        List<Task> taskList = getTaskList(message);

        try {
            int index = Integer.parseInt(message.getText());

            taskList.remove(index - 1);
        } catch (NumberFormatException exc){
            throw new NumberFormatException("Input argument isn't INTEGER");
        }

    }

    public void changeTask(Message message, int index){
        List<Task> taskList = getTaskList(message);

        taskList.remove(index - 1);
        taskList.add(index - 1, new Task(message.getText()));
    }

}
