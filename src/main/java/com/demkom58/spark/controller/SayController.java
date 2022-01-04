package com.demkom58.spark.controller;

import com.demkom58.telegram.mvc.annotations.BotController;
import com.demkom58.telegram.mvc.annotations.CommandMapping;
import com.demkom58.telegram.mvc.message.MessageType;
import com.demkom58.telegram.mvc.message.TelegramMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@BotController
public class SayController {
    @CommandMapping(
            value = {"/say {input}"},
            event = MessageType.TEXT_MESSAGE
    )
    public SendMessage groups(TelegramMessage message, String input) {
        return SendMessage.builder()
                .chatId(String.valueOf(message.getChatId()))
                .text("Говорю тебе: " + input)
                .build();
    }
}
