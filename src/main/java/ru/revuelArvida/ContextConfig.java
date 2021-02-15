package ru.revuelArvida;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

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
        return  new PomodoroBot(name, token);
    }


}
