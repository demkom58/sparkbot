package com.demkom58.telegram.mvc.controller;

import com.demkom58.telegram.mvc.annotations.CommandMapping;

import java.lang.reflect.Method;

public interface ApiMethodControllerFactory {
    BotCommandController create(CommandMapping mapping, Object bean, Method method);
}
