package com.demkom58.spark.controller;

import com.demkom58.spark.entity.User;
import com.demkom58.spark.service.UserService;
import com.demkom58.springram.controller.annotation.BotController;
import com.demkom58.springram.controller.annotation.Chain;
import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.annotation.ExceptionHandler;
import com.demkom58.springram.controller.message.ChatTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.lang.reflect.InvocationTargetException;

@BotController
public class SayController {
    private static final Logger log = LoggerFactory.getLogger(SayController.class);
    private static final String MESSAGE_INPUT_CHAIN = "say/message-input";
    private static final String MESSAGE_PREMIUM_INPUT_CHAIN = "say/prem-message-input";
    private final UserService userService;

    public SayController(UserService userService) {
        this.userService = userService;
    }

    @ExceptionHandler(InvocationTargetException.class)
    public SendMessage onAccessDenied(InvocationTargetException e, ChatTextMessage message) {
        return SendMessage.builder()
                .chatId(String.valueOf(message.getChatId()))
                .text("Not enough permissions!")
                .build();
    }

    @CommandMapping("say")
    public SendMessage say(ChatTextMessage message) {
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
    public SendMessage sayInput(ChatTextMessage message) {
        final User user = userService.getUser(message.getFromUser().getId());
        user.setChain(null);
        userService.saveUser(user);

        return SendMessage.builder()
                .chatId(String.valueOf(message.getChatId()))
                .text("Awesome! You typed: " + message.getText())
                .build();
    }

    @CommandMapping("psay")
    @PreAuthorize("hasRole('ROLE_TG_PREMIUM')")
    public SendMessage psay(ChatTextMessage message) {
        final User user = userService.getUser(message.getFromUser().getId());
        user.setChain(MESSAGE_PREMIUM_INPUT_CHAIN);
        userService.saveUser(user);

        return SendMessage.builder()
                .chatId(String.valueOf(message.getChatId()))
                .text("(For premium) Send message now")
                .build();
    }

    @CommandMapping
    @Chain(MESSAGE_PREMIUM_INPUT_CHAIN)
    @PreAuthorize("hasRole('ROLE_TG_PREMIUM')")
    public SendMessage sayPremiumInput(ChatTextMessage message) {
        final User user = userService.getUser(message.getFromUser().getId());
        user.setChain(null);
        userService.saveUser(user);

        return SendMessage.builder()
                .chatId(String.valueOf(message.getChatId()))
                .text("Awesome Premium! You typed: " + message.getText())
                .build();
    }
}
