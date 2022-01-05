package com.demkom58.telegram.config;

import com.demkom58.telegram.TelegramLongPollingMvcBot;
import com.demkom58.telegram.mvc.CommandContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
@Import({TelegramProperties.class, TelegramBotsApiFactory.class})
public class TelegramBotApiConfig {
    private final TelegramBotsApiFactory apiFactory;

    public TelegramBotApiConfig(TelegramBotsApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return apiFactory.create();
    }

    @Bean
    @ConditionalOnMissingBean(TelegramLongPollingBot.class)
    public TelegramLongPollingBot defaultLongPollingBot(@Value("${bot.token}") String token,
                                                        @Value("${bot.username}") String username,
                                                        CommandContainer container) {
        return new TelegramLongPollingMvcBot(username, token, container);
    }
}
