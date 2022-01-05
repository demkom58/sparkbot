package com.demkom58.telegram.mvc;

import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class CommandResult {
    private final List<BotApiMethod<?>> methods;
    private final Function<Update, CommandResult> interceptor;
    private final Function<User, CommandResult> onObsolete;
    private final boolean saveOld;

    public CommandResult(@Nullable final List<BotApiMethod<?>> methods,
                         @Nullable final Function<Update, CommandResult> interceptor,
                         @Nullable final Function<User, CommandResult> onObsolete,
                         boolean saveOld) {
        this.methods = methods;
        this.interceptor = interceptor;
        this.onObsolete = onObsolete;
        this.saveOld = saveOld;
    }

    public void execute(Consumer<BotApiMethod<?>> executor) {
        if (methods != null)
            methods.forEach(executor);
    }

    @Nullable
    public List<BotApiMethod<?>> getMethods() {
        return methods;
    }

    @Nullable
    public Function<Update, CommandResult> getInterceptor() {
        return interceptor;
    }

    public CommandResult intercept(Update update) {
        if (interceptor != null)
            return interceptor.apply(update);

        return CommandResult.empty();
    }

    @Nullable
    public Function<User, CommandResult> getOnObsolete() {
        return onObsolete;
    }

    public CommandResult obsolete(User user) {
        if (onObsolete != null)
            return onObsolete.apply(user);

        return CommandResult.empty();
    }

    public boolean isSaveOld() {
        return saveOld;
    }

    public static CommandResult empty() {
        return new CommandResult(null, null, null, false);
    }

    public static  CommandResult simple(BotApiMethod<?> method) {
        return new CommandResult(List.of(method), null, null, false);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<BotApiMethod<?>> methods = new ArrayList<>();
        private Function<Update, CommandResult> interceptor;
        private Function<User, CommandResult> onObsolete;
        private boolean saveOld = false;

        private Builder() { }

        public Builder method(BotApiMethod<?> method) {
            this.methods.add(method);
            return this;
        }

        public Builder methods(Collection<BotApiMethod<?>> methods) {
            this.methods.addAll(methods);
            return this;
        }

        public Builder intercept(Function<Update, CommandResult> interceptor) {
            this.interceptor = interceptor;
            return this;
        }

        public Builder obsolete(Function<User, CommandResult> onObsolete) {
            this.onObsolete = onObsolete;
            return this;
        }

        public Builder saveOld() {
            this.saveOld = true;
            return this;
        }

        public CommandResult build() {
            return new CommandResult(methods, interceptor, onObsolete, saveOld);
        }
    }

}
