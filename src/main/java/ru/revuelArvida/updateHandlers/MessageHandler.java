package ru.revuelArvida.updateHandlers;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.revuelArvida.PomodoroBot;
import ru.revuelArvida.PomodoroBotApp;
import ru.revuelArvida.States;
import ru.revuelArvida.timer.Pomodoro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class MessageHandler implements UpdateHandler {


    private final PomodoroBot bot;
    private Map<String, Pomodoro> settings = new HashMap<>();

    @Autowired
    public MessageHandler(PomodoroBot bot){
        this.bot = bot;
    }

    @Override
    public void handle(Update update) {
        if (bot.getState() == States.WAIT)                  {handleWait(update);}
        if (bot.getState() == States.SETTINGS)              {handleSettings(update);}
        if (bot.getState() == States.WORK)                  {}
        if (bot.getState() == States.PERSONALIZED_SETTINGS) {handlePersonalizedSettings(update);}

    }

    public void handleWait(Update update){
        Message message = update.getMessage();
        String text = message.getText();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());

        switch (text) {
            case "/start" -> {
                sendMessage.setText("Привет, я твой личный тайм менеджер, сообщу тебе когда ты в " +
                        "помидоре, а когда следует отдохнуть!");

                bot.sendMessage(sendMessage);

                bot.setState(States.SETTINGS);
                sendMessage.setText("Произведем первоначальные настройки. \nНастройки по " +
                        "умолчанию: " +
                        "\nРабочий период - " +
                        "\nКороткий перерыв - " +
                        "\nДлинный перерыв - " +
                        "\nХотите персонализировать периоды или оставить настройки по-умолчанию?");
                bot.sendMessage(sendMessage);
            }
            case "Запустить таймер" -> {

            }

            case "Показать настройки" -> {
                Pomodoro pomodoroSettings = settings.get(message.getChatId().toString());
                sendMessage.setText("Рабочий период - " + pomodoroSettings.getWorkPeriod() + " мин."
                + "\n Короткий перерыв - " + pomodoroSettings.getShortBrakePeriod() + " мин. " +
                        "\n Длинный перерыв - " + pomodoroSettings.getLongBrakePeriod() + " мин.");
                bot.sendMessage(sendMessage);
            }
        }
    }

    private void handleSettings(Update update){
        Message message = update.getMessage();
        String text = message.getText();

        Pomodoro pomodoroSettings = settings.get(message.getChatId().toString());

        if(pomodoroSettings == null){
            pomodoroSettings = PomodoroBotApp.getCtx().getBean(Pomodoro.class);
            settings.put(message.getChatId().toString(),pomodoroSettings);
        }


        switch (text){
            case "Установить настройки по умолчанию" ->{

            }
            case "Установить свои настройки" -> {
                bot.setState(States.PERSONALIZED_SETTINGS);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Отлично, введите пожалуйста продолжительность рабочего " +
                        "периода:");
                sendMessage.setChatId(message.getChatId().toString());
                bot.sendMessage(sendMessage);
            }
        }
    }

    private void handlePersonalizedSettings(Update update){
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        String text = message.getText();
        Pomodoro pomodoroSettings = settings.get(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if( pomodoroSettings.getWorkPeriod() == 0){
            pomodoroSettings.setWorkPeriod(Integer.parseInt(text)) ;
            text = null;
            sendMessage.setText("Введите длительность короткого перерыва: ");
            bot.sendMessage(sendMessage);
        } else if(pomodoroSettings.getShortBrakePeriod() == 0){
            pomodoroSettings.setShortBrakePeriod(Integer.parseInt(text));
            text = null;
            sendMessage.setText("Введите длительность длинного перерыва: ");
            bot.sendMessage(sendMessage);
        } else if (pomodoroSettings.getLongBrakePeriod() == 0){
            pomodoroSettings.setLongBrakePeriod(Integer.parseInt(text));
            text = null;
            bot.setState(States.WAIT);
            sendMessage.setText("Настройка закончена, возврат в главное меню");
            bot.sendMessage(sendMessage);
        }
    }


}
