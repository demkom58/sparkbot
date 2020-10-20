package com.demkom58.spark.mvc.controller;

import com.demkom58.spark.mvc.CommandResult;
import com.demkom58.spark.mvc.annotations.CommandMapping;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class BotCommandController implements CommandController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotCommandController.class);

    private final CommandMapping mapping;
    private final Object bean;
    private final Method method;

    public BotCommandController(CommandMapping mapping, Object bean, Method method) {
        this.mapping = mapping;
        this.bean = bean;
        this.method = method;

        if (!CommandResult.class.equals(method.getReturnType()))
            throw new IllegalArgumentException(
                    "Method '" + method.getName() + "' " +
                            "of class '" + bean.getClass().getName() + "' " +
                            "returns not " + CommandResult.class.getName()
            );

    }

    public abstract boolean successUpdatePredicate(Update update);

    @Override
    public List<CommandResult> process(Update update) {
        if (!successUpdatePredicate(update))
            return null;

        try {
            return processSingle(update);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("bad invoke method", e);
        }

        return null;
    }

    public CommandMapping getMapping() {
        return mapping;
    }

    boolean typeListReturnDetect() {
        return CommandResult.class.equals(method.getReturnType());
    }

    private List<CommandResult> processSingle(Update update) throws InvocationTargetException, IllegalAccessException {
        CommandResult botApiMethod = (CommandResult) method.invoke(bean, update);
        return botApiMethod != null
                ? Collections.singletonList(botApiMethod)
                : new ArrayList<>(0);
    }

}
