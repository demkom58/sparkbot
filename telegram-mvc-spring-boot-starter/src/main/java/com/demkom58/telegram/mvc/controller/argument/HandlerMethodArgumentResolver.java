package com.demkom58.telegram.mvc.controller.argument;

import com.demkom58.telegram.mvc.message.TelegramMessage;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface HandlerMethodArgumentResolver {
    boolean isSupported(MethodParameter parameter);

    @Nullable
    Object resolve(MethodParameter parameter, TelegramMessage message, AbsSender bot) throws Exception;
}
