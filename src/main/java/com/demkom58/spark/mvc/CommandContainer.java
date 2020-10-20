package com.demkom58.spark.mvc;

import com.demkom58.spark.mvc.annotations.CommandMapping;
import com.demkom58.spark.mvc.controller.BotCommandController;
import com.demkom58.spark.mvc.controller.CommandController;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.*;
import java.util.function.Consumer;

@Component
public class CommandContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandContainer.class);
    private final Map<EventType, Multimap<String, CommandController>> controllerMap =
            new EnumMap<>(new HashMap<EventType, Multimap<String, CommandController>>() {{
                for (EventType value : EventType.pathMethods())
                    put(value, Multimaps.newSetMultimap(new HashMap<>(), HashSet::new));
            }});
    private final Collection<CommandController> pathlessControllers = new HashSet<>();
    private final CommandInterceptorStorage interceptorStorage;
    private Consumer<BotApiMethod<?>> executor = (m) -> {};

    public CommandContainer(@NotNull final CommandInterceptorStorage interceptorStorage) {
        this.interceptorStorage = interceptorStorage;
    }

    public void addBotControllers(Collection<String> paths, Collection<BotCommandController> controller) {
        paths.forEach(path -> controller.forEach(c -> addBotController(path, c)));
    }

    public void addBotControllers(String path, Collection<BotCommandController> controller) {
        controller.forEach(c -> addBotController(path, c));
    }

    public void addBotController(String path, BotCommandController controller) {
        final CommandMapping mapping = controller.getMapping();
        final EventType[] eventTypes = mapping.event();

        final boolean pathOnly = mapping.pathOnly();

        for (EventType eventType : eventTypes) {
            final boolean canHasPath = eventType.canHasPath();
            if (!pathOnly)
                pathlessControllers.add(controller);

            if (canHasPath) {
                LOGGER.trace("add telegram bot controller for path: {}", path);
                controllerMap.get(eventType).put(path, controller);
            }
        }
    }

    @NotNull
    public void handle(@NotNull final Update update) {
        final User user = extractUser(update);

        if (user != null) {
            final Optional<CommandResult> commandResult = interceptorStorage.processIntercept(user, update);
            if (commandResult.isPresent()) {
                final CommandResult interceptorResult = commandResult.get().intercept(update);
                interceptorResult.execute(executor);

                if (!interceptorResult.isSaveOld())
                    interceptorStorage.drop(user);

                if (interceptorResult.getInterceptor() != null)
                    interceptorStorage.add(user, interceptorResult);

                return;
            }
        }

        Collection<CommandController> controllers;
        if (update.hasMessage()) {
            controllers = findControllers(EventType.TEXT_MESSAGE, update.getMessage().getText());
        } else if (update.hasEditedMessage()) {
            controllers = findControllers(EventType.TEXT_MESSAGE_EDIT, update.getEditedMessage().getText());
        } else if (update.hasChannelPost()) {
            controllers = findControllers(EventType.TEXT_POST, update.getChannelPost().getText());
        } else if (update.hasEditedChannelPost()) {
            controllers = findControllers(EventType.TEXT_POST_EDIT, update.getEditedMessage().getText());
        } else {
            controllers = pathlessControllers;
        }

        if (controllers == null)
            controllers = Collections.emptyList();

        for (CommandController c : controllers)
            for (CommandResult result : c.process(update)) {
                result.execute(executor);
                if (user != null && result.getInterceptor() != null)
                    interceptorStorage.add(user, result);
            }
    }

    private Collection<CommandController> findControllers(EventType method, String message) {
        if (message != null) {
            final var categoryMap = controllerMap.get(method);
            var controllers = categoryMap.get(message);

            if (controllers.isEmpty())
                controllers = categoryMap.get(getPath(message));

            return controllers.isEmpty() ? categoryMap.get("") : controllers;
        }

        return pathlessControllers;
    }

    public String getPath(String message) {
        return message.split(" ", 1)[0].trim().toLowerCase();
    }

    @Nullable
    public static User extractUser(Update update) {
        if (update.hasMessage())
            return update.getMessage().getFrom();

        if (update.hasCallbackQuery())
            return update.getCallbackQuery().getFrom();

        if (update.hasPollAnswer())
            return update.getPollAnswer().getUser();

        if (update.hasEditedMessage())
            return update.getEditedChannelPost().getFrom();

        if (update.hasInlineQuery())
            return update.getInlineQuery().getFrom();

        if (update.hasShippingQuery())
            return update.getShippingQuery().getFrom();

        return null;
    }

    public void setExecutor(Consumer<BotApiMethod<?>> executor) {
        this.executor = executor;
        this.interceptorStorage.setExecutor(executor);
    }
}
