package com.demkom58.spark.bot;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class SparkBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(SparkBot.class);

    @Getter
    private final String botToken;
    @Getter
    private final String botUsername;

    public SparkBot(@Value("${bot.token}") String token,
                    @Value("${bot.username}") String username) {
        this.botToken = token;
        this.botUsername = username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage())
            return;

        var message = update.getMessage();
        var response = new SendMessage();

        var chatId = message.getChatId();
        response.setChatId(chatId);

        var text = message.getText();
        response.setText(text);

        try {
            execute(response);
            LOGGER.info("Sent message '{}' to {}", text, chatId);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send message '{}' to {} due to error: {}", text, chatId, e.getMessage());
        }
    }

}
