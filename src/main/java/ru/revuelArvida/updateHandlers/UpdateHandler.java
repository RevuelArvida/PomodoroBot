package ru.revuelArvida.updateHandlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {
    public void handle(Update update);
}
