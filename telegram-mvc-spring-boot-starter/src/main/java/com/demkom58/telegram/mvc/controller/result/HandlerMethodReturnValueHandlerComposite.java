package com.demkom58.telegram.mvc.controller.result;

import com.demkom58.telegram.mvc.message.TelegramMessage;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class HandlerMethodReturnValueHandlerComposite implements HandlerMethodReturnValueHandler {
    private final List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>();

    public void add(HandlerMethodReturnValueHandler resolver) {
        this.handlers.add(resolver);
    }

    public void addAll(HandlerMethodReturnValueHandler... resolvers) {
        this.handlers.addAll(Arrays.asList(resolvers));
    }

    public void addAll(Collection<HandlerMethodReturnValueHandler> resolvers) {
        this.handlers.addAll(resolvers);
    }

    @Override
    public boolean isSupported(MethodParameter returnType) {
        return getReturnValueHandler(returnType) != null;
    }

    @Override
    public void handle(MethodParameter returnType, TelegramMessage message, AbsSender bot, Object result) throws Exception {
        final HandlerMethodReturnValueHandler handler = getReturnValueHandler(returnType);
        Assert.notNull(handler,
                "Return value handler not found! It should be checked with isSupported() first.");
        handler.handle(returnType, message, bot, result);
    }

    @Nullable
    private HandlerMethodReturnValueHandler getReturnValueHandler(MethodParameter returnType) {
        for (var handler : this.handlers) {
            if (handler.isSupported(returnType)) {
                return handler;
            }
        }
        return null;
    }
}
