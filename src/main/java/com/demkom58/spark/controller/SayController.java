package com.demkom58.spark.controller;

import com.demkom58.spark.entity.User;
import com.demkom58.spark.service.UserService;
import com.demkom58.springram.controller.annotation.BotController;
import com.demkom58.springram.controller.annotation.Chain;
import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.message.TelegramMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@BotController
public class SayController {
    private static final String MESSAGE_INPUT_CHAIN = "say/message-input";
    private final UserService userService;

    public SayController(UserService userService) {
        this.userService = userService;
    }

    @CommandMapping("say")
    public SendMessage say(TelegramMessage message) {
        final User user = userService.getUser(message.getFromUser().getId());
        user.setChain(MESSAGE_INPUT_CHAIN);
        userService.saveUser(user);

        return SendMessage.builder()
                .chatId(String.valueOf(message.getChatId()))
                .text("Send message now")
                .build();
    }

    @CommandMapping
    @Chain(MESSAGE_INPUT_CHAIN)
    public SendMessage sayInput(TelegramMessage message) {
        final User user = userService.getUser(message.getFromUser().getId());
        user.setChain("default");
        userService.saveUser(user);

        return SendMessage.builder()
                .chatId(String.valueOf(message.getChatId()))
                .text("Awesome! You typed: " + message.getText())
                .build();
    }
}
