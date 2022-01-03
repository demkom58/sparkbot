package com.demkom58.telegram;

import com.demkom58.telegram.mvc.CommandContainer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
@Getter
public class TelegramLongPollingMvcBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final CommandContainer container;

    @Override
    public void onUpdateReceived(Update update) {
        container.handle(update);
        container.setExecutor(botApiMethod -> {
            try {
                this.execute(botApiMethod);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }
}
