package com.demkom58.spark.mvc;

import com.demkom58.spark.mvc.controller.ApiMethodControllerFactory;
import com.demkom58.spark.mvc.controller.SimpleCommandController;

import java.util.Arrays;

public enum EventType {
    ANY(((mapping, bean, method)
            -> new SimpleCommandController(mapping, bean, method, u -> true)), false),

    TEXT_MESSAGE((mapping, bean, method)
            -> new SimpleCommandController(mapping, bean, method, u -> u.hasMessage() && u.getMessage().hasText()), true),
    TEXT_MESSAGE_EDIT(((mapping, bean, method)
            -> new SimpleCommandController(mapping, bean, method, u -> u.hasEditedMessage() && u.getEditedMessage().hasText())), true),

    TEXT_POST(((mapping, bean, method)
            -> new SimpleCommandController(mapping, bean, method, u -> u.hasChannelPost() && u.getChannelPost().hasText())), true),
    TEXT_POST_EDIT(((mapping, bean, method)
            -> new SimpleCommandController(mapping, bean, method, u -> u.hasEditedChannelPost() && u.getEditedChannelPost().hasText())), true),
;
    private static final EventType[] pathMethods
            = Arrays.stream(values()).filter(EventType::canHasPath).toArray(EventType[]::new);
    private static final EventType[] noPathMethods
            = Arrays.stream(values()).filter(b -> !b.canHasPath).toArray(EventType[]::new);


    private final ApiMethodControllerFactory controllerFactory;
    private final boolean canHasPath;

    EventType(ApiMethodControllerFactory controllerFactory, boolean canHasPath) {
        this.controllerFactory = controllerFactory;
        this.canHasPath = canHasPath;
    }

    public ApiMethodControllerFactory getControllerFactory() {
        return controllerFactory;
    }

    public boolean canHasPath() {
        return canHasPath;
    }

    public static EventType[] pathMethods() {
        return pathMethods;
    }

    public static EventType[] noPathMethods() {
        return noPathMethods;
    }
}
