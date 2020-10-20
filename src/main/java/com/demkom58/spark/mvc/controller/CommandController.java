package com.demkom58.spark.mvc.controller;

import com.demkom58.spark.mvc.CommandResult;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandController {
    @Nullable
    CommandResult process(Update update);
}
