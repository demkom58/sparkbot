package com.demkom58.telegram.mvc;

import com.demkom58.telegram.mvc.config.PathMatchingConfigurer;
import com.demkom58.telegram.mvc.controller.HandlerMapping;
import com.demkom58.telegram.mvc.controller.TelegramMessageHandler;
import com.demkom58.telegram.mvc.controller.TelegramMessageHandlerMethod;
import com.demkom58.telegram.mvc.controller.result.HandlerMethodReturnValueHandlerComposite;
import com.demkom58.telegram.mvc.message.MessageType;
import com.demkom58.telegram.mvc.message.TelegramMessage;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.lang.reflect.Method;
import java.util.*;

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

    private final PathMatchingConfigurer pathMatchingConfigurer;
    private final PathMatcher pathMatcher;

    private HandlerMethodReturnValueHandlerComposite returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();

    public CommandContainer(final PathMatchingConfigurer pathMatchingConfigurer) {
        Objects.requireNonNull(pathMatchingConfigurer, "PathMatchingConfigurer can't be null!");

        this.pathMatchingConfigurer = pathMatchingConfigurer;
        this.pathMatcher = pathMatchingConfigurer.getPathMatcher();
    }

    public void setReturnValueHandlers(final HandlerMethodReturnValueHandlerComposite returnValueHandlers) {
        this.returnValueHandlers = returnValueHandlers;
    }

    public void addBotController(final String path,
                                 final TelegramMessageHandlerMethod controller) {
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

    @SneakyThrows
    public void handle(@NonNull final Update update, @NonNull final AbsSender bot) {
        Objects.requireNonNull(update, "Update can't be null!");
        Objects.requireNonNull(bot, "Receiver bot can't be null!");

        final TelegramMessage message = TelegramMessage.from(update);
        final MessageType eventType = message.getEventType();
        if (eventType == null) {
            return;
        }

        final String messageText = message.getText();
        TelegramMessageHandler handler = findControllers(eventType, messageText);
        if (handler == null) {
            return;
        }

        final Map<String, String> variables = pathMatcher
                .extractUriTemplateVariables(handler.getMapping().value(), messageText);

        message.setAttribute("variables", variables);
        final Object result = handler.invoke(message, bot, message, bot);
        if (result == null) {
            return;
        }

        final MethodParameter returnType = handler.getReturnType();
        final boolean supported = returnValueHandlers.isSupported(returnType);
        if (supported) {
            returnValueHandlers.handle(returnType, message, bot, result);
        } else {
            throw new UnsupportedOperationException("Unsupported return type '" +
                    returnType.getParameterType().getName() + "' in method '" + returnType.getMethod() + "'");
        }
    }

    private TelegramMessageHandler findControllers(final MessageType method, final String message) {
        if (StringUtils.hasText(message)) {
            var directHandler = directMap.get(method).get(message.toLowerCase());
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
}
