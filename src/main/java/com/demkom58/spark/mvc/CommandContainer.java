package com.demkom58.spark.mvc;

import com.demkom58.spark.mvc.annotations.CommandMapping;
import com.demkom58.spark.mvc.controller.BotCommandController;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

@Component
public class CommandContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandContainer.class);
    private final Map<EventType, Multimap<String, BotCommandController>> controllerMap =
            new EnumMap<>(new HashMap<EventType, Multimap<String, BotCommandController>>() {{
                for (EventType value : EventType.pathMethods())
                    put(value, Multimaps.newSetMultimap(new HashMap<>(), HashSet::new));
            }});
    private final Collection<BotCommandController> pathlessControllers = new HashSet<>();

    private CommandContainer() {
    }

    public void addBotControllers(Collection<String> paths, Collection<BotCommandController> controller) {
        paths.forEach(path -> controller.forEach(c -> addBotController(path, c)));
    }

    public void addBotControllers(String path, Collection<BotCommandController> controller) {
        controller.forEach(c -> addBotController(path, c));
    }

    public void addBotController(String path, BotCommandController controller) {
        final CommandMapping mapping = controller.getMapping();
        final EventType[] methods = mapping.event();

        final boolean pathOnly = mapping.pathOnly();

        for (EventType method : methods) {
            final boolean canHasPath = method.canHasPath();
            if (!pathOnly)
                pathlessControllers.add(controller);

            if (canHasPath) {
                LOGGER.trace("add telegram bot controller for path: {}", path);
                controllerMap.get(method).put(path, controller);
            }
        }
    }

    @NotNull
    public Collection<BotCommandController> getHandle(@NotNull final Update update) {
        Collection<BotCommandController> controller = null;

        if (update.hasMessage()) {
            controller = findControllers(EventType.TEXT_MESSAGE, update.getMessage().getText());
        } else if (update.hasEditedMessage()) {
            controller = findControllers(EventType.TEXT_MESSAGE_EDIT, update.getEditedMessage().getText());
        } else if (update.hasChannelPost()) {
            controller = findControllers(EventType.TEXT_POST, update.getChannelPost().getText());
        } else if (update.hasEditedChannelPost()) {
            controller = findControllers(EventType.TEXT_POST_EDIT, update.getEditedMessage().getText());
        } else  {
            return pathlessControllers;
        }

        return controller == null ? Collections.emptyList() : controller;
    }

    private Collection<BotCommandController> findControllers(EventType method, String message) {
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

}
