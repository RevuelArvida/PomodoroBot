package ru.revuelArvida;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.revuelArvida.updateHandlers.CallBackQueryHandler;
import ru.revuelArvida.updateHandlers.MessageHandler;


@Configuration
@ComponentScan
@PropertySource("classpath:TelegramBot.properties")
public class ContextConfig {

    @Value("${pomodoroBot.name}")
    private String name;
    @Value("${pomodoroBot.token}")
    private String token;

    @Bean
    @Scope("singleton")
    public PomodoroBot pomodoroBot(){
        PomodoroBot bot = new PomodoroBot(name, token);
        return bot;
    }


}
