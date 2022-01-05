package com.demkom58.telegram.mvc.config;

import com.demkom58.telegram.mvc.CommandContainer;
import com.demkom58.telegram.mvc.CommandInterceptorStorage;
import com.demkom58.telegram.mvc.UpdateBeanPostProcessor;
import com.demkom58.telegram.mvc.controller.argument.HandlerMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

import java.util.List;

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
    public UpdateBeanPostProcessor updateBeanPostProcessor(CommandContainer container,
                                                           List<HandlerMethodArgumentResolver> argumentResolvers) {
        return new UpdateBeanPostProcessor(container, argumentResolvers);
    }
}
