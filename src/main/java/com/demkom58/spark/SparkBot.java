package com.demkom58.spark;

import com.demkom58.springram.controller.TelegramCommandDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class SparkBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final TelegramCommandDispatcher commandDispatcher;

    public SparkBot(@Value("${bot.username}") String botUsername,
                    @Value("${bot.token}") String botToken,
                    TelegramCommandDispatcher commandDispatcher) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            commandDispatcher.dispatch(update, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}