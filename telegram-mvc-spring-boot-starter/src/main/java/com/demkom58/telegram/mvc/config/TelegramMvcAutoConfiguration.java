package com.demkom58.telegram.mvc.config;

import com.demkom58.telegram.mvc.CommandContainer;
import com.demkom58.telegram.mvc.CommandInterceptorStorage;
import com.demkom58.telegram.mvc.UpdateBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@Configuration
public class TelegramMvcAutoConfiguration {
    @Bean
    public CommandInterceptorStorage commandInterceptorStorage() {
        return new CommandInterceptorStorage();
    }

    @Bean
    public CommandContainer commandContainer(CommandInterceptorStorage storage) {
        return new CommandContainer(storage, new AntPathMatcher());
    }

    @Bean
    public UpdateBeanPostProcessor updateBeanPostProcessor(CommandContainer container) {
        return new UpdateBeanPostProcessor(container);
    }
}
