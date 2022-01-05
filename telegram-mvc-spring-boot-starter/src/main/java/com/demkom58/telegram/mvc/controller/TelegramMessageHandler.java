package com.demkom58.telegram.mvc.controller;

import com.demkom58.telegram.mvc.message.TelegramMessage;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface TelegramMessageHandler {
    @Nullable
    Object invoke(@NonNull TelegramMessage message, @NonNull AbsSender bot, Object... providedArgs) throws Exception;

    HandlerMapping getMapping();

    MethodParameter getReturnType();
}
