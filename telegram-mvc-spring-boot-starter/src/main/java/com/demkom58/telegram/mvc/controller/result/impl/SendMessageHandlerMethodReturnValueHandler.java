package com.demkom58.telegram.mvc.controller.result.impl;

import com.demkom58.telegram.mvc.controller.result.HandlerMethodReturnValueHandler;
import com.demkom58.telegram.mvc.message.TelegramMessage;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class SendMessageHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override
    public boolean isSupported(MethodParameter returnType) {
        return SendMessage.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handle(MethodParameter returnType, TelegramMessage message, AbsSender bot, Object result) throws Exception {
        final SendMessage sm = (SendMessage) result;
        bot.execute(sm);
    }
}