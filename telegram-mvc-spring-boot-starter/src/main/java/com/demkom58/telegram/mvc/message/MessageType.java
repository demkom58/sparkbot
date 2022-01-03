package com.demkom58.telegram.mvc.message;

import com.demkom58.telegram.mvc.controller.ApiMethodControllerFactory;
import com.demkom58.telegram.mvc.controller.SimpleCommandController;

import java.util.Arrays;

public enum MessageType {
    TEXT_MESSAGE(SimpleCommandController::new, true),
    TEXT_MESSAGE_EDIT(SimpleCommandController::new, true),
    TEXT_POST(SimpleCommandController::new, true),
    TEXT_POST_EDIT(SimpleCommandController::new, true),
;

    private static final MessageType[] pathMethods
            = Arrays.stream(values()).filter(MessageType::canHasPath).toArray(MessageType[]::new);
    private static final MessageType[] pathlessMethods
            = Arrays.stream(values()).filter(b -> !b.canHasPath).toArray(MessageType[]::new);

    private final ApiMethodControllerFactory controllerFactory;
    private final boolean canHasPath;

    MessageType(ApiMethodControllerFactory controllerFactory, boolean canHasPath) {
        this.controllerFactory = controllerFactory;
        this.canHasPath = canHasPath;
    }

    public ApiMethodControllerFactory getControllerFactory() {
        return controllerFactory;
    }

    public boolean canHasPath() {
        return canHasPath;
    }

    public static MessageType[] pathMethods() {
        return pathMethods;
    }

    public static MessageType[] pathlessMethods() {
        return pathlessMethods;
    }
}
