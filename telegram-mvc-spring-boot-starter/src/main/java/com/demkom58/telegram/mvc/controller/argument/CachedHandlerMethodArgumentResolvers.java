package com.demkom58.telegram.mvc.controller.argument;

import com.demkom58.telegram.mvc.message.TelegramMessage;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CachedHandlerMethodArgumentResolvers implements HandlerMethodArgumentResolver {
    private final List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
    private final Map<MethodParameter, HandlerMethodArgumentResolver> cachedResolvers = new ConcurrentHashMap<>();

    public void addResolver(HandlerMethodArgumentResolver resolver) {
        this.resolvers.add(resolver);
    }

    public void addResolvers(HandlerMethodArgumentResolver... resolvers) {
        this.resolvers.addAll(Arrays.asList(resolvers));
    }

    public void addResolvers(Collection<HandlerMethodArgumentResolver> resolvers) {
        this.resolvers.addAll(resolvers);
    }

    @Override
    public boolean isSupported(MethodParameter parameter) {
        return getArgumentResolver(parameter) != null;
    }

    @Override
    public Object resolve(MethodParameter parameter, TelegramMessage message, AbsSender bot) throws Exception {
        final HandlerMethodArgumentResolver resolver = getArgumentResolver(parameter);
        if (resolver == null) {
            throw new IllegalArgumentException(
                    "Unsupported parameter type '" + parameter.getParameterType().getName() + "'"
            );
        }

        return resolver.resolve(parameter, message, bot);
    }

    private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
        final HandlerMethodArgumentResolver cached = cachedResolvers.get(parameter);

        if (cached == null) {
            for (HandlerMethodArgumentResolver resolver : resolvers) {
                if (resolver.isSupported(parameter)) {
                    cachedResolvers.put(parameter, resolver);
                    return resolver;
                }
            }
        }

        return cached;
    }
}
