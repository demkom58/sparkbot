package com.demkom58.spark.controller;

import com.demkom58.springram.controller.annotation.BotController;
import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.annotation.PathVariable;
import com.demkom58.springram.controller.message.MessageType;
import com.demkom58.springram.controller.message.TelegramMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@BotController
public class SayController {
    @CommandMapping(
            value = {"/say {input}"},
            event = MessageType.TEXT_MESSAGE
    )
    public SendMessage say(TelegramMessage message, @PathVariable String input) {
        return SendMessage.builder()
                .chatId(String.valueOf(message.getChatId()))
                .text(input)
                .build();
    }
}
