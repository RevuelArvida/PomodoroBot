package ru.revuelArvida;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class PomodoroBotApp {


    public static void main(String[] args) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(ContextConfig.class);

        PomodoroBot bot = ctx.getBean(PomodoroBot.class);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException exc){
            exc.printStackTrace();
        }

    }

}
