package com.demkom58.telegram.mvc.controller;

import java.lang.reflect.Method;

public interface ApiMethodControllerFactory {
    TelegramMessageHandlerMethod create(HandlerMapping mapping, Object bean, Method method);
}
