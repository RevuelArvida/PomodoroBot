package ru.revuelArvida.updateHandlers;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.revuelArvida.PomodoroBot;
import ru.revuelArvida.PomodoroBotApp;
import ru.revuelArvida.States;
import ru.revuelArvida.task.Task;
import ru.revuelArvida.task.TaskManager;
import ru.revuelArvida.timer.Pomodoro;
import ru.revuelArvida.timer.PomodoroSettings;
import ru.revuelArvida.timer.PomodoroState;

import java.util.HashMap;
import java.util.Map;


@Component
@Scope("singleton")
public class MessageHandler implements UpdateHandler {

    private final PomodoroBot bot;
    private final Map<String, PomodoroSettings> settingsMap = new HashMap<>();
    private final Map<String, SendMessage> sendMessageMap = new HashMap<>();
    private final Map<String, Pomodoro> pomodoroMap = new HashMap<>();
    private final Map<String, Integer> indexMap = new HashMap<>();
    private final TaskManager taskManager;

    private static final int DEFAULT_WORK_PERIOD = 25;
    private static final int DEFAULT_SHORT_BREAK_PERIOD = 5;
    private static final int DEFAULT_LONG_BREAK_PERIOD = 15;

    @Autowired
    public MessageHandler(PomodoroBot bot, TaskManager taskManager){
        this.bot = bot;
        this.taskManager = taskManager;
    }

    @Override
    public void handle(Update update) {

        setUser(update);

        if (bot.getState() == States.WAIT)                  {handleWait(update.getMessage());}
        if (bot.getState() == States.SETTINGS)              {handleSettings(update.getMessage());}
        if (bot.getState() == States.PERSONALIZED_SETTINGS) {handlePersonalizedSettings(update.getMessage());}
        if (bot.getState() == States.TASK_LIST)             {handleTaskList(update.getMessage());}
        if (bot.getState() == States.WORK)                  {handleWork(update.getMessage());}
        if (bot.getState() == States.ADD_TASK ||
                bot.getState() == States.ADD_TASK_AT_BEGIN ||
                bot.getState() == States.CHANGE_TASK ||
                bot.getState() == States.ADD_TASK_AT_INDEX ||
                bot.getState() == States.DELETE_TASK ||
                bot.getState() == States.CHANGE_TASK_INDEX) {handleTaskListChanges(update.getMessage());}

    }

    private void setUser(Update update){

        SendMessage sendMessage = sendMessageMap.get(update.getMessage().getChatId().toString());

        if (sendMessage == null) {
            sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId().toString());
            sendMessageMap.put(update.getMessage().getChatId().toString(), sendMessage);
        }

        Pomodoro pomodoro = pomodoroMap.get(update.getMessage().getChatId().toString());

        if (pomodoro == null) {
            pomodoro = PomodoroBotApp.getCtx().getBean(Pomodoro.class);
            pomodoroMap.put(update.getMessage().getChatId().toString(), pomodoro);
        }

        PomodoroSettings pomodoroSettings =
                settingsMap.get(update.getMessage().getChatId().toString());

        if (pomodoroSettings == null){
            pomodoroSettings = PomodoroBotApp.getCtx().getBean(PomodoroSettings.class);
            settingsMap.put(update.getMessage().getChatId().toString(), pomodoroSettings);
        }
    }

    public void handleWait(Message message){
        String text = message.getText();
        SendMessage sendMessage = sendMessageMap.get(message.getChatId().toString());
        Pomodoro pomodoro = pomodoroMap.get(message.getChatId().toString());
        PomodoroSettings pomodoroSettings = settingsMap.get(message.getChatId().toString());

        switch (text) {
            case "/start" -> {
                sendMessage.setText("Привет, я твой личный тайм менеджер, сообщу тебе когда ты в " +
                        "помидоре, а когда следует отдохнуть!");

                bot.sendMessage(sendMessage);

                bot.setState(States.SETTINGS);
                sendMessage.setText("Произведем первоначальные настройки: \nНастройки по " +
                        "умолчанию: " +
                        "\nРабочий период - " + DEFAULT_WORK_PERIOD +
                        "\nКороткий перерыв - " + DEFAULT_SHORT_BREAK_PERIOD +
                        "\nДлинный перерыв - " + DEFAULT_LONG_BREAK_PERIOD +
                        "\nХотите персонализировать периоды или оставить настройки по-умолчанию?");
                bot.sendMessage(sendMessage);
            }
            case "Начать работу" -> {
                bot.setState(States.WORK);
                sendMessage.setText("Пора поработать, я сообщу, когда сделать перерыв");
                bot.sendMessage(sendMessage);

                pomodoro.setPomodoroSettings(pomodoroSettings);
                pomodoro.setSendMessage(sendMessage);

                try {
                    pomodoro.startWork();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }


            }

            case "Настройки" -> {
                sendMessage.setText("Текущие настройки:" + "\nРабочий период - " +
                        pomodoroSettings.getWorkPeriod(false) + " " + "мин."
                        + "\nКороткий перерыв - " + pomodoroSettings.getShortBrakePeriod(false) + " " +
                        "мин. " +
                        "\nДлинный перерыв - " + pomodoroSettings.getLongBrakePeriod(false) + " мин.");
                bot.sendMessage(sendMessage);
            }

            case "Задачи" -> {

                if (taskManager.getTaskList(message) == null){

                    sendMessage.setText("На текущий момент у вас нет задач, чтобы добавить первую" +
                            " задачу, нажмите \"Добавить задачу\". \nЧтобы вернуться в главное " +
                            "меню," +
                            " нажмите \"Выход\"");
                    bot.setState(States.TASK_LIST);

                } else {

                    StringBuilder tasks = new StringBuilder();
                    int i = 0;
                    for (Task task: taskManager.getTaskList(message)){
                        ++i;
                        tasks.append(i + ". " + task.getTask() + "\n");
                    }

                    sendMessage.setText("Вот ваши задачи на текущий момент: \n" + tasks.toString());
                }
                bot.sendMessage(sendMessage);

            }
        }
    }

    private void handleSettings(Message message){
        String text = message.getText();
        SendMessage sendMessage = sendMessageMap.get(message.getChatId().toString());
        PomodoroSettings pomodoroSettings = settingsMap.get(message.getChatId().toString());

        switch (text){
            case "Установить настройки по умолчанию" ->{
                pomodoroSettings.setWorkPeriod(DEFAULT_WORK_PERIOD);
                pomodoroSettings.setShortBrakePeriod(DEFAULT_SHORT_BREAK_PERIOD);
                pomodoroSettings.setLongBrakePeriod(DEFAULT_LONG_BREAK_PERIOD);
                bot.setState(States.WAIT);
                sendMessage.setText("Настройка закончена, возвращаюсь в главное меню");
                bot.sendMessage(sendMessage);
            }

            case "Установить свои настройки" -> {
                bot.setState(States.PERSONALIZED_SETTINGS);
                sendMessage.setText("Отлично, введите пожалуйста продолжительность рабочего " +
                        "периода:");

                bot.sendMessage(sendMessage);
            }

            /**
             * To be Deleted (For test use only)
             */
            case"Тест" -> {
                pomodoroSettings.setWorkPeriod(1);
                pomodoroSettings.setShortBrakePeriod(1);
                pomodoroSettings.setLongBrakePeriod(1);
                bot.setState(States.WAIT);
                sendMessage.setText("Настройка закончена, возвращаюсь в главное меню");
                bot.sendMessage(sendMessage);
            }
        }
    }

    private void handlePersonalizedSettings(Message message){
        String text = message.getText();
        SendMessage sendMessage = sendMessageMap.get(message.getChatId().toString());
        PomodoroSettings pomodoroSettings = settingsMap.get(message.getChatId().toString());


        if( pomodoroSettings.getWorkPeriod(true) == 0){
            pomodoroSettings.setWorkPeriod(Integer.parseInt(text)) ;
            text = null;

            sendMessage.setText("Введите длительность короткого перерыва: ");
            bot.sendMessage(sendMessage);

        } else if(pomodoroSettings.getShortBrakePeriod(true) == 0 & text != null){
            pomodoroSettings.setShortBrakePeriod(Integer.parseInt(text));
            text = null;

            sendMessage.setText("Введите длительность длинного перерыва: ");
            bot.sendMessage(sendMessage);

        } else if (pomodoroSettings.getLongBrakePeriod(true) == 0 & text != null){
            pomodoroSettings.setLongBrakePeriod(Integer.parseInt(text));
            text = null;

            bot.setState(States.WAIT);
            sendMessage.setText("Настройка закончена, возвращаюсь в главное меню");
            bot.sendMessage(sendMessage);
        }
    }

    private void handleTaskList(Message message){

        String text = message.getText();
        SendMessage sendMessage = sendMessageMap.get(message.getChatId().toString());

        switch (text){

            case "Добавить задачу" ->{
                bot.setState(States.ADD_TASK);
                sendMessage.setText("Введите вашу задачу: ");
                bot.sendMessage(sendMessage);
            }

            case "Добавить задачу в начало" -> {
                bot.setState(States.ADD_TASK_AT_BEGIN);
                sendMessage.setText("Введите вашу задачу: ");
                bot.sendMessage(sendMessage);
            }

            case "Добавить задачу по номеру" -> {
                bot.setState(States.ADD_TASK_AT_INDEX);
                sendMessage.setText("Введите индекс на котором должна быть задача: ");
                bot.sendMessage(sendMessage);
            }

            case "Изменить задачу" -> {
                bot.setState(States.CHANGE_TASK_INDEX);
                sendMessage.setText("Введите индекс задачи, которую хотите изменить: ");
                bot.sendMessage(sendMessage);
            }

            case "Удалить задачу" ->{
                bot.setState(States.DELETE_TASK);
                sendMessage.setText("Введите индек задачи, которую хотите удалить: ");
                bot.sendMessage(sendMessage);
            }

        }

    }

    private void handleTaskListChanges(Message message){
        SendMessage sendMessage = sendMessageMap.get(message.getChatId().toString());

        if (bot.getState() == States.ADD_TASK){

            taskManager.createTaskAtEnd(message);
            sendMessage.setText("Задача добавлена!");

            bot.setState(States.TASK_LIST);
            bot.sendMessage(sendMessage);

        } else if (bot.getState() == States.ADD_TASK_AT_BEGIN) {

            taskManager.createTaskAtBegin(message);
            sendMessage.setText("Задача добавлена!");

            bot.setState(States.TASK_LIST);
            bot.sendMessage(sendMessage);

        } else if (bot.getState() == States.ADD_TASK_INDEX) {

            indexMap.put(message.getChatId().toString(), Integer.parseInt(message.getText()));
            sendMessage.setText("Введите вашу задачу: ");

            bot.setState(States.ADD_TASK_AT_INDEX);
            bot.sendMessage(sendMessage);

        } else if (bot.getState() == States.ADD_TASK_AT_INDEX){

            taskManager.createTaskAtIndex(message, indexMap.get(message.getChatId().toString()));
            sendMessage.setText("Задача добавлена!");

            bot.setState(States.TASK_LIST);
            bot.sendMessage(sendMessage);

        } else if (bot.getState() == States.CHANGE_TASK_INDEX){

            indexMap.put(message.getChatId().toString(), Integer.parseInt(message.getText()));
            sendMessage.setText("Введите вашу задачу: ");

            bot.setState(States.CHANGE_TASK);
            bot.sendMessage(sendMessage);

        } else if (bot.getState() == States.CHANGE_TASK){

            taskManager.changeTask(message,  indexMap.get(message.getChatId()));
            sendMessage.setText("Задача изменена!");

            bot.setState(States.TASK_LIST);
            bot.sendMessage(sendMessage);

        } else if (bot.getState() == States.DELETE_TASK){

            taskManager.deleteTask(message);
            sendMessage.setText("Задача удалена!");

            bot.setState(States.TASK_LIST);
            bot.sendMessage(sendMessage);

        }

    }

    private void handleWork(Message message){
        String text = message.getText();
        Pomodoro pomodoro = pomodoroMap.get(message.getChatId().toString());

        switch (text){
            case "Начать перерыв" -> {

                try {
                    pomodoro.startBreak();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

            case "Продолжить работу" -> {
                try {
                    pomodoro.startWork();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

            case "Пауза" -> {
                if (pomodoro.getState() == PomodoroState.WORK){
                    pomodoro.cancelTimer();
                }
            }

            case "Продолжить" -> {
                if (pomodoro.getState() == PomodoroState.WORK) {
                    pomodoro.resume();
                }
            }

            case "Остановить работу" -> {
                pomodoro.cancelTimer();
                bot.setState(States.WAIT);
            }
        }


    }


}
