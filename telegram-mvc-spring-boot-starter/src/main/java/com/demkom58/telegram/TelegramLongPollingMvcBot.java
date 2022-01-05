package com.demkom58.telegram;

import com.demkom58.telegram.mvc.CommandContainer;
import lombok.Getter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Getter
public class TelegramLongPollingMvcBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final CommandContainer container;

    public TelegramLongPollingMvcBot(String botUsername, String botToken, CommandContainer container) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.container = container;

        container.setExecutor(botApiMethod -> {
            try {
                this.execute(botApiMethod);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onUpdateReceived(Update update) {
        container.handle(update, this);
    }
}
