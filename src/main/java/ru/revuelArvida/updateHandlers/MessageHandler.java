package ru.revuelArvida.updateHandlers;


import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.revuelArvida.PomodoroBot;


@Component
public class MessageHandler implements UpdateHandler {


    private final PomodoroBot bot;

    @Autowired
    public MessageHandler(PomodoroBot bot){
        this.bot = bot;
    }

    @Override
    public void handle(Update update) {
        Message message = update.getMessage();
        String text = message.getText();

        switch (text){
            case "/start" -> {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Привет, я твой личный тайм менеджер, сообщу тебе когда ты в " +
                        "помидоре, а когда следует отдохнуть!");
                sendMessage.setChatId(message.getChatId().toString());
                bot.sendMessage(sendMessage);
            }
        }
    }

}
