package com.demkom58.spark.controller;

import com.demkom58.springram.controller.annotation.BotController;
import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.message.ChatTextMessage;
import com.demkom58.springram.controller.message.MessageType;
import com.demkom58.springram.controller.message.SpringramMessage;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@BotController
public class PollController {
    @CommandMapping(event = MessageType.POLL_ANSWER)
    public SendMessage pollAnswer(SpringramMessage message) {
        return SendMessage.builder()
                .chatId(String.valueOf(message.getFromUser().getId()))
                .text("Answer handled!")
                .build();
    }

    @CommandMapping("poll")
    public SendPoll poll(ChatTextMessage message) {
        return SendPoll.builder()
                .chatId(String.valueOf(message.getChatId()))
                .isAnonymous(false)
                .allowSendingWithoutReply(false)
                .question("Sex?")
                .option("Option 1")
                .option("Option 2")
                .option("Option 3")
                .option("Option 4")
                .option("Option 5")
                .build();
    }
}
