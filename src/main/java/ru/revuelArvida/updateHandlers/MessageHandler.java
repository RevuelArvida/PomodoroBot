package ru.revuelArvida.updateHandlers;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@NoArgsConstructor
public class MessageHandler implements UpdateHandler {


    @Override
    public void handle(Update update) {

    }

}
