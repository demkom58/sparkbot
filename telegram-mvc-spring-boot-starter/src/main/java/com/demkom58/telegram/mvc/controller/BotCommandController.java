package com.demkom58.telegram.mvc.controller;

import com.demkom58.telegram.mvc.CommandResult;
import com.demkom58.telegram.mvc.annotations.CommandMapping;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

public abstract class BotCommandController implements CommandController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotCommandController.class);

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

    public abstract boolean successUpdatePredicate(Update update);

    @Override
    @Nullable
    public CommandResult process(Update update) {
        if (!successUpdatePredicate(update))
            return null;

        try {
            return handler.handle(update);
        } catch (ReflectiveOperationException e) {
            LOGGER.error("bad invoke method", e);
        }

        return null;
    }

    public Handler createHandler() {
        final Class<?> returnType = method.getReturnType();

        if (CommandResult.class.isAssignableFrom(returnType))
            return (u) -> (CommandResult) method.invoke(bean, u);

        if (BotApiMethod.class.isAssignableFrom(returnType))
            return (u) -> CommandResult.simple((BotApiMethod<?>) method.invoke(bean, u));

        if (Collection.class.isAssignableFrom(returnType)) {
            ParameterizedType collectionType = (ParameterizedType) method.getGenericReturnType();
            ParameterizedType actualTypeArgument = (ParameterizedType) collectionType.getActualTypeArguments()[0];
            try {
                if (BotApiMethod.class.isAssignableFrom(Class.forName(actualTypeArgument.getRawType().getTypeName())))
                    return (u) -> new CommandResult(
                            (List<BotApiMethod<?>>) method.invoke(bean, u), null, null, false
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
        @Nullable CommandResult handle(Update update) throws ReflectiveOperationException;
    }

}
