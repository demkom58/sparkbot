package com.demkom58.spark.bot;

import com.demkom58.spark.mvc.CommandContainer;
import com.demkom58.spark.mvc.CommandResult;
import com.demkom58.spark.mvc.controller.BotCommandController;
import com.demkom58.spark.mvc.controller.CommandController;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Getter
public class SparkBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(SparkBot.class);
    private final CommandContainer container;

    private final String botToken;
    private final String botUsername;

    private final User account;

    public SparkBot(CommandContainer container,
                    @Value("${bot.token}") String token,
                    @Value("${bot.username}") String username) {
        this.container = container;
        this.container.setExecutor((response) -> {
            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });

        this.botToken = token;
        this.botUsername = username;

        this.account = getBotAccount().orElseGet(() -> {
            System.exit(1);
            return null;
        });
    }

    @Override
    public void onUpdateReceived(Update update) {
        container.handle(update);
    }

    @Bean(name = "botAccount")
    public User getAccount() {
        return account;
    }

    private Optional<User> getBotAccount() {
        User me = null;
        try {
            me = getMe();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(me);
    }

}
