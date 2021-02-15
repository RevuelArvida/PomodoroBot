package ru.revuelArvida;


import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.revuelArvida.updateHandlers.CallBackQueryHandler;
import ru.revuelArvida.updateHandlers.MessageHandler;
import ru.revuelArvida.updateHandlers.UpdateHandler;


public class PomodoroBot extends TelegramLongPollingBot {

    private String botUsername;
    private String botToken;
    private UpdateHandler messageHandler;
    private UpdateHandler callBackQueryHandler;


    public PomodoroBot (String botUsername, String botToken){
        this.botUsername = botUsername;
        this.botToken = botToken;

        this.messageHandler = PomodoroBotApp.getCtx().getBean(MessageHandler.class);
        this.callBackQueryHandler = PomodoroBotApp.getCtx().getBean(CallBackQueryHandler.class);;
    }


    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()){
            messageHandler.handle(update);
        } else if(update.hasCallbackQuery()){
            callBackQueryHandler.handle(update);
        }
    }


    public void sendMessage(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
