package com.demkom58.spark.controller;

import com.demkom58.telegram.mvc.annotations.BotController;
import com.demkom58.telegram.mvc.annotations.CommandMapping;
import com.demkom58.telegram.mvc.message.MessageType;
import com.demkom58.telegram.mvc.message.TelegramMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@BotController
public class StartController {
    private final ReplyKeyboardMarkup menuKeyboardMarkup = new ReplyKeyboardMarkup();

    public StartController() {
        final var read = new KeyboardRow();
        read.addAll(List.of("Группы", "Заявки"));
        final var write = new KeyboardRow();
        write.addAll(List.of("Отправить", "Управление"));
        final var other = new KeyboardRow();
        other.addAll(List.of("Донат"));

        menuKeyboardMarkup.setSelective(true);
        menuKeyboardMarkup.setResizeKeyboard(true);
        menuKeyboardMarkup.setOneTimeKeyboard(false);
        menuKeyboardMarkup.setKeyboard(List.of(read, write, other));
    }

    @CommandMapping(
            value = {"/start", "Start", "Старт", "/menu", "Menu", "Меню"},
            event = MessageType.TEXT_MESSAGE
    )
    public SendMessage start(TelegramMessage message) {
        return SendMessage.builder()
                .chatId(String.valueOf(message.getChatId()))
                .text("Отобразил тебе меню :)")
                .replyMarkup(menuKeyboardMarkup)
                .build();
    }

}

