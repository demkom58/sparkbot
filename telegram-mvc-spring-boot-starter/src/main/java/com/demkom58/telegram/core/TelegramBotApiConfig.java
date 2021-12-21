package com.demkom58.telegram.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
}
