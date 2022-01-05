package com.demkom58.telegram.mvc.controller.argument.impl;

import com.demkom58.telegram.mvc.annotation.PathVariable;
import com.demkom58.telegram.mvc.controller.argument.HandlerMethodArgumentResolver;
import com.demkom58.telegram.mvc.message.TelegramMessage;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Map;

public class PathVariablesHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean isSupported(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PathVariable.class);
    }

    @Override
    @Nullable
    public Object resolve(MethodParameter parameter, TelegramMessage message, AbsSender bot) throws Exception {
        final Object variablesObject = message.getAttribute("variables");

        if (variablesObject instanceof Map<?, ?> variablesMap) {
            String variableName = parameter.getParameterAnnotation(PathVariable.class).value();
            if (variableName.isEmpty()) {
                variableName = parameter.getParameterName();
            }

            return variablesMap.get(variableName);
        }

        return null;
    }
}
