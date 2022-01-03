package com.demkom58.telegram.mvc.controller;

import com.demkom58.telegram.mvc.annotations.CommandMapping;
import com.demkom58.telegram.mvc.message.TelegramMessage;

import java.lang.reflect.Method;

public class SimpleCommandController extends BotCommandController {

    public SimpleCommandController(CommandMapping mapping, Object bean, Method method) {
        super(mapping, bean, method);
    }

    @Override
    public boolean isSupportedMessage(TelegramMessage message) {
        return true;
    }
}
