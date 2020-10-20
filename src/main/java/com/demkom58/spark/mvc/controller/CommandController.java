package com.demkom58.spark.mvc.controller;

import com.demkom58.spark.mvc.CommandResult;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface CommandController {
    List<CommandResult> process(Update update);
}
