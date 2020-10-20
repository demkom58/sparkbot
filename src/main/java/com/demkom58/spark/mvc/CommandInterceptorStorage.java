package com.demkom58.spark.mvc;

import com.demkom58.spark.mvc.CommandResult;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
public class CommandInterceptorStorage {
    private final Cache<User, CommandResult> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .removalListener(a -> {
                final User key = (User) a.getKey();
                final CommandResult value = (CommandResult) a.getValue();
                if (!a.wasEvicted())
                    return;

                final CommandResult obsolete = value.obsolete(key);
                obsolete.execute(getExecutor());

                if (obsolete.isSaveOld())
                    getCache().put(key, value);
                else if (obsolete.getInterceptor() != null)
                    getCache().put(key, obsolete);

            }).build();

    private Consumer<BotApiMethod<?>> executor = (m) -> {};

    public void add(User user, CommandResult commandResult) {
        cache.put(user, commandResult);
    }

    public void drop(User user) {
        cache.invalidate(user);
    }

    public Optional<CommandResult> processIntercept(User user, Update update) {
        final boolean hasMessage = update.hasMessage();
        if (!hasMessage)
            return Optional.empty();

        return Optional.ofNullable(cache.getIfPresent(user));
    }

    public Cache<User, CommandResult> getCache() {
        return cache;
    }

    public void setExecutor(Consumer<BotApiMethod<?>> executor) {
        this.executor = executor;
    }

    public Consumer<BotApiMethod<?>> getExecutor() {
        return executor;
    }
}
