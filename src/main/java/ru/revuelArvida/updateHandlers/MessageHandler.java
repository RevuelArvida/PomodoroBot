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
import ru.revuelArvida.timer.Pomodoro;
import ru.revuelArvida.timer.PomodoroSettings;
import ru.revuelArvida.timer.PomodoroState;

import javax.xml.bind.PropertyException;
import java.util.HashMap;
import java.util.Map;


@Component
@Scope("singleton")
public class MessageHandler implements UpdateHandler {

    private final PomodoroBot bot;
    private Map<String, PomodoroSettings> settingsMap = new HashMap<>();
    private Map<String, SendMessage> sendMessageMap = new HashMap<>();
    private Map<String, Pomodoro> pomodoroMap = new HashMap<>();

    private static final int DEFAULT_WORK_PERIOD = 25;
    private static final int DEFAULT_SHORT_BREAK_PERIOD = 5;
    private static final int DEFAULT_LONG_BREAK_PERIOD = 15;

    @Autowired
    public MessageHandler(PomodoroBot bot){
        this.bot = bot;
    }

    @Override
    public void handle(Update update) {

        setUser(update);

        if (bot.getState() == States.WAIT)                  {handleWait(update.getMessage());}
        if (bot.getState() == States.SETTINGS)              {handleSettings(update.getMessage());}
        if (bot.getState() == States.PERSONALIZED_SETTINGS) {handlePersonalizedSettings(update.getMessage());}
        if (bot.getState() == States.TASK_LIST)             {handleTaskList(update.getMessage());}
        if (bot.getState() == States.WORK)                  {handleWork(update.getMessage());}

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
                } catch (PropertyException e) {
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

    }

    private void handleWork(Message message){
        String text = message.getText();
        Pomodoro pomodoro = pomodoroMap.get(message.getChatId().toString());

        switch (text){
            case "Начать перерыв" -> {

                try {
                    pomodoro.startBreak();
                } catch (PropertyException e) {
                    e.printStackTrace();
                }
            }

            case "Продолжить работу" -> {
                try {
                    pomodoro.startWork();
                } catch (PropertyException e) {
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
