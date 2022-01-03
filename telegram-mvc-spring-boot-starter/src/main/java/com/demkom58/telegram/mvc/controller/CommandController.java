package com.demkom58.telegram.mvc.controller;

import com.demkom58.telegram.mvc.CommandResult;
import com.demkom58.telegram.mvc.message.TelegramMessage;
import org.jetbrains.annotations.Nullable;

public interface CommandController {
    @Nullable
    CommandResult process(TelegramMessage message);
}
