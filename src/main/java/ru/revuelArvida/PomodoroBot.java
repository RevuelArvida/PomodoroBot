package ru.revuelArvida;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.revuelArvida.updateHandlers.UpdateHandler;

@Component
@Scope("singleton")
public class PomodoroBot extends TelegramLongPollingBot {

    private String botUsername;
    private String botToken;
    @Autowired
    private UpdateHandler messageHandler;
    @Autowired
    private UpdateHandler callBackQueryHandler;
    private States state = States.WAIT;


    public PomodoroBot (String botUsername, String botToken){
        this.botUsername = botUsername;
        this.botToken = botToken;
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


    public void sendMessage(SendMessage sendMessage){
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public States getState() {
        return state;
    }

    public void setState(States state) {
        this.state = state;
    }

}
