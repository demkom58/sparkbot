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
        final AntPathMatcher pathMatcher = new AntPathMatcher(" ");
        pathMatcher.setCaseSensitive(false);
        return new CommandContainer(storage, pathMatcher);
    }

    @Bean
    public UpdateBeanPostProcessor updateBeanPostProcessor(CommandContainer container) {
        return new UpdateBeanPostProcessor(container);
    }
}
