package com.demkom58.telegram.mvc;

import com.demkom58.telegram.mvc.annotations.CommandMapping;
import com.demkom58.telegram.mvc.controller.TelegramMessageHandlerMethod;
import com.demkom58.telegram.mvc.controller.TelegramMessageHandler;
import com.demkom58.telegram.mvc.message.MessageType;
import com.demkom58.telegram.mvc.message.TelegramMessage;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.*;
import java.util.function.Consumer;

@Component
@Slf4j
public class CommandContainer {
    private final Map<MessageType, Multimap<String, TelegramMessageHandler>> controllerMap =
            new EnumMap<>(new HashMap<MessageType, Multimap<String, TelegramMessageHandler>>() {{
                for (MessageType value : MessageType.pathMethods())
                    put(value, Multimaps.newSetMultimap(new HashMap<>(), HashSet::new));
            }});
    private final Collection<TelegramMessageHandler> pathlessControllers = new HashSet<>();
    private final CommandInterceptorStorage interceptorStorage;
    private Consumer<BotApiMethod<?>> executor = (m) -> {
    };

    public CommandContainer(@NotNull final CommandInterceptorStorage interceptorStorage) {
        this.interceptorStorage = interceptorStorage;
    }

    public void addBotControllers(Collection<String> paths, Collection<TelegramMessageHandlerMethod> controller) {
        paths.forEach(path -> controller.forEach(c -> addBotController(path, c)));
    }

    public void addBotControllers(String path, Collection<TelegramMessageHandlerMethod> controller) {
        controller.forEach(c -> addBotController(path, c));
    }

    public void addBotController(String path, TelegramMessageHandlerMethod controller) {
        final CommandMapping mapping = controller.getMapping();
        final MessageType[] eventTypes = mapping.event();

        final boolean pathOnly = mapping.pathOnly();

        for (MessageType eventType : eventTypes) {
            final boolean canHasPath = eventType.canHasPath();
            if (!pathOnly)
                pathlessControllers.add(controller);

            if (canHasPath) {
                log.trace("add telegram bot controller for path: {}", path);
                controllerMap.get(eventType).put(path, controller);
            }
        }
    }

    public void handle(@NotNull final Update update) {
        final TelegramMessage message = TelegramMessage.from(update);
        if (message.getEventType() == null) {
            return;
        }

        final User fromUser = message.getFromUser();
        if (fromUser != null) {
            final Optional<CommandResult> commandResult = interceptorStorage.processIntercept(fromUser, update);
            if (commandResult.isPresent()) {
                final CommandResult interceptorResult = commandResult.get().intercept(update);
                interceptorResult.execute(executor);

                if (!interceptorResult.isSaveOld())
                    interceptorStorage.drop(fromUser);

                if (interceptorResult.getInterceptor() != null)
                    interceptorStorage.add(fromUser, interceptorResult);

                return;
            }
        }

        Collection<TelegramMessageHandler> controllers = findControllers(message.getEventType(), message.getText());
        for (TelegramMessageHandler c : controllers) {
            CommandResult result = c.handle(message);
            if (result == null)
                continue;

            result.execute(executor);
            if (fromUser != null && result.getInterceptor() != null)
                interceptorStorage.add(fromUser, result);
        }
    }

    private Collection<TelegramMessageHandler> findControllers(MessageType method, String message) {
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

    public void setExecutor(Consumer<BotApiMethod<?>> executor) {
        this.executor = executor;
        this.interceptorStorage.setExecutor(executor);
    }
}
