package com.demkom58.telegram.mvc.config;

import com.demkom58.telegram.mvc.CommandContainer;
import com.demkom58.telegram.mvc.CommandInterceptorStorage;
import com.demkom58.telegram.mvc.UpdateBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramMvcConfiguration {
    @Bean
    public CommandInterceptorStorage commandInterceptorStorage() {
        return new CommandInterceptorStorage();
    }

    @Bean
    public CommandContainer commandContainer(CommandInterceptorStorage storage) {
        return new CommandContainer(storage);
    }

    @Bean
    public UpdateBeanPostProcessor updateBeanPostProcessor(CommandContainer container) {
        return new UpdateBeanPostProcessor(container);
    }
}
