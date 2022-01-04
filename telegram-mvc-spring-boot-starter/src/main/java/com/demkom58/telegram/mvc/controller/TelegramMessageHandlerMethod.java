package com.demkom58.telegram.mvc.controller;

import com.demkom58.telegram.mvc.CommandResult;
import com.demkom58.telegram.mvc.annotations.CommandMapping;
import com.demkom58.telegram.mvc.message.TelegramMessage;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

@Slf4j
public class TelegramMessageHandlerMethod implements TelegramMessageHandler {
    private final CommandMapping mapping;
    private final Object bean;
    private final Method method;
    private final Method protoMethod;

    private final Handler handler;

    public TelegramMessageHandlerMethod(CommandMapping mapping, Object bean, Method method) {
        this.mapping = mapping;
        this.bean = bean;
        this.method = method;
        this.protoMethod = BridgeMethodResolver.findBridgedMethod(method);
        ReflectionUtils.makeAccessible(protoMethod);
        this.handler = createHandler();
    }

    @Override
    @Nullable
    public CommandResult handle(TelegramMessage message) {
        try {
            return handler.handle(message);
        } catch (ReflectiveOperationException e) {
            log.error("bad invoke method", e);
        }
        return null;
    }

    public Handler createHandler() {
        final Method method = protoMethod;
        final Class<?> returnType = method.getReturnType();

        if (CommandResult.class.isAssignableFrom(returnType))
            return (message) -> (CommandResult) method.invoke(bean, message);

        if (BotApiMethod.class.isAssignableFrom(returnType))
            return (message) -> CommandResult.simple((BotApiMethod<?>) method.invoke(bean, message));

        if (Collection.class.isAssignableFrom(returnType)) {
            ParameterizedType collectionType = (ParameterizedType) method.getGenericReturnType();
            ParameterizedType actualTypeArgument = (ParameterizedType) collectionType.getActualTypeArguments()[0];
            try {
                if (BotApiMethod.class.isAssignableFrom(Class.forName(actualTypeArgument.getRawType().getTypeName())))
                    return (message) -> new CommandResult(
                            (List<BotApiMethod<?>>) method.invoke(bean, message), null, null, false
                    );
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        throw new IllegalArgumentException(
                "Method '" + this.method.getName() + "' of class '" + bean.getClass().getName() + "' returns invalid type. "
        );
    }

    public CommandMapping getMapping() {
        return mapping;
    }

    public interface Handler {
        @Nullable CommandResult handle(TelegramMessage message) throws ReflectiveOperationException;
    }

}
