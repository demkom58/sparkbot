package com.demkom58.telegram.mvc.config;

import com.demkom58.telegram.mvc.CommandContainer;
import com.demkom58.telegram.mvc.UpdateBeanPostProcessor;
import com.demkom58.telegram.mvc.controller.argument.HandlerMethodArgumentResolver;
import com.demkom58.telegram.mvc.controller.result.HandlerMethodReturnValueHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

import java.util.List;

@Configuration
public class TelegramMvcAutoConfiguration {
    @Bean
    public CommandContainer commandContainer() {
        final AntPathMatcher pathMatcher = new AntPathMatcher(" ");
        pathMatcher.setCaseSensitive(false);
        return new CommandContainer(pathMatcher);
    }

    @Bean
    public UpdateBeanPostProcessor updateBeanPostProcessor(CommandContainer container,
                                                           List<HandlerMethodArgumentResolver> argumentResolvers,
                                                           List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        return new UpdateBeanPostProcessor(container, argumentResolvers, returnValueHandlers);
    }
}
