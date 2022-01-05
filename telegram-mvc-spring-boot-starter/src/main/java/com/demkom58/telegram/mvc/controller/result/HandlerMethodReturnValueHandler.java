package com.demkom58.telegram.mvc.controller.result;

import com.demkom58.telegram.mvc.message.TelegramMessage;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface HandlerMethodReturnValueHandler {
    boolean isSupported(MethodParameter returnType);

    void handle(MethodParameter returnType, TelegramMessage message, AbsSender bot, Object result) throws Exception;
}
