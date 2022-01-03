package com.demkom58.telegram.mvc.controller;

import com.demkom58.telegram.mvc.CommandResult;
import com.demkom58.telegram.mvc.annotations.CommandMapping;
import com.demkom58.telegram.mvc.message.TelegramMessage;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

@Slf4j
public abstract class BotCommandController implements CommandController {
    private final CommandMapping mapping;
    private final Object bean;
    private final Method method;

    private final Handler handler;

    public BotCommandController(CommandMapping mapping, Object bean, Method method) {
        this.mapping = mapping;
        this.bean = bean;
        this.method = method;
        this.handler = createHandler();
    }

    public abstract boolean isSupportedMessage(TelegramMessage message);

    @Override
    @Nullable
    public CommandResult process(TelegramMessage message) {
        if (!isSupportedMessage(message))
            return null;

        try {
            return handler.handle(message);
        } catch (ReflectiveOperationException e) {
            log.error("bad invoke method", e);
        }

        return null;
    }

    public Handler createHandler() {
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
                "Method '" + method.getName() + "' of class '" + bean.getClass().getName() + "' returns invalid type. "
        );
    }

    public CommandMapping getMapping() {
        return mapping;
    }

    boolean typeListReturnDetect() {
        return CommandResult.class.equals(method.getReturnType());
    }

    public interface Handler {
        @Nullable CommandResult handle(TelegramMessage message) throws ReflectiveOperationException;
    }

}
