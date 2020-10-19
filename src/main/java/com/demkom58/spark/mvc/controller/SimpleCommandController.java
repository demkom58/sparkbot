package com.demkom58.spark.mvc.controller;

import com.demkom58.spark.mvc.annotations.CommandMapping;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.function.Predicate;

public class SimpleCommandController extends BotCommandController {
    private final Predicate<Update> successPredicate;

    public SimpleCommandController(CommandMapping mapping, Object bean,
                                   Method method, Predicate<@NotNull Update> successPredicate) {
        super(mapping, bean, method);
        this.successPredicate = successPredicate;
    }

    @Override
    public boolean successUpdatePredicate(Update update) {
        if (update == null)
            return false;

        return successPredicate.test(update);
    }
}
