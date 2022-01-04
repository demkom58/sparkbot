package com.demkom58.telegram.mvc;

import com.demkom58.telegram.mvc.controller.HandlerMapping;
import com.demkom58.telegram.mvc.controller.TelegramMessageHandler;
import com.demkom58.telegram.mvc.controller.TelegramMessageHandlerMethod;
import com.demkom58.telegram.mvc.message.MessageType;
import com.demkom58.telegram.mvc.message.TelegramMessage;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
public class CommandContainer {
    private final Map<MessageType, Map<String, TelegramMessageHandler>> directMap =
            Maps.newEnumMap(new HashMap<MessageType, Map<String, TelegramMessageHandler>>() {{
                for (MessageType value : MessageType.pathMethods())
                    put(value, Maps.newHashMap());
            }});
    private final Map<MessageType, Map<String, TelegramMessageHandler>> patternMap =
            Maps.newEnumMap(new HashMap<MessageType, Map<String, TelegramMessageHandler>>() {{
                for (MessageType value : MessageType.pathMethods())
                    put(value, Maps.newHashMap());
            }});

    private final CommandInterceptorStorage interceptorStorage;
    private Consumer<BotApiMethod<?>> executor = (m) -> {
    };
    private final PathMatcher pathMatcher;

    public CommandContainer(@NotNull final CommandInterceptorStorage interceptorStorage,
                            @NotNull final PathMatcher pathMatcher) {
        this.interceptorStorage = interceptorStorage;
        this.pathMatcher = pathMatcher;
    }

    public void addBotController(String path, TelegramMessageHandlerMethod controller) {
        final HandlerMapping mapping = controller.getMapping();
        final Method mtd = controller.getMethod();
        final MessageType[] eventTypes = mapping.messageTypes();

        for (MessageType messageType : eventTypes) {
            final boolean canHasPath = messageType.canHasPath();
            if (canHasPath) {
                log.trace("Adding method handler for message type {} with path: {}", messageType, path);
                if (pathMatcher.isPattern(path)) {
                    final var prev = patternMap.get(messageType).putIfAbsent(path, controller);
                    if (prev != null) {
                        throw new IllegalStateException(
                                "Cant register handler with pattern mapping '" + path
                                        + "' for method '" + mtd.getName() + "'"
                        );
                    }
                } else {
                    final var prev = directMap.get(messageType).putIfAbsent(path, controller);
                    if (prev != null) {
                        throw new IllegalStateException(
                                "Cant register handler with direct mapping '" + path
                                        + "' for method '" + mtd.getName() + "'"
                        );
                    }
                }
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

                if (!interceptorResult.isSaveOld()) {
                    interceptorStorage.drop(fromUser);
                }

                if (interceptorResult.getInterceptor() != null) {
                    interceptorStorage.add(fromUser, interceptorResult);
                }

                return;
            }
        }

        TelegramMessageHandler handler = findControllers(message.getEventType(), message.getText().toLowerCase());
        if (handler == null) {
            return;
        }

        CommandResult result = handler.handle(message);
        if (result == null) {
            return;
        }

        result.execute(executor);
        if (fromUser != null && result.getInterceptor() != null) {
            interceptorStorage.add(fromUser, result);
        }
    }

    private TelegramMessageHandler findControllers(MessageType method, String message) {
        if (StringUtils.hasText(message)) {
            var directHandler = directMap.get(method).get(message);
            if (directHandler != null) {
                return directHandler;
            }

            final List<TelegramMessageHandler> handlers = new ArrayList<>();
            final var entries = patternMap.get(method).entrySet();
            for (Map.Entry<String, TelegramMessageHandler> entry : entries) {
                final String key = entry.getKey();
                if (pathMatcher.match(key, message)) {
                    handlers.add(entry.getValue());
                }
            }

            if (handlers.isEmpty()) {
                return null;
            }

            final Comparator<String> patternComparator = pathMatcher.getPatternComparator(message);
            handlers.sort((c1, c2) -> patternComparator.compare(
                    c1.getMapping().value(),
                    c2.getMapping().value()
            ));

            return handlers.get(0);
        }

        return null;
    }

    public void setExecutor(Consumer<BotApiMethod<?>> executor) {
        this.executor = executor;
        this.interceptorStorage.setExecutor(executor);
    }
}
