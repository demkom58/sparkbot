package com.demkom58.telegram.mvc.message;

import lombok.Data;
import lombok.ToString;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@ToString
public class TelegramMessage {
    private final Update update;

    private final MessageType eventType;
    private final User fromUser;
    private final Long chatId;
    private final Chat chat;
    private final String text;

    private final Map<String, Object> attributes = new HashMap<>();

    public TelegramMessage(Update update, MessageType eventType, User fromUser, Long chatId, Chat chat, String text) {
        this.update = update;
        this.eventType = eventType;
        this.fromUser = fromUser;
        this.chatId = chatId;
        this.chat = chat;
        this.text = text;
    }

    @Nullable
    public static TelegramMessage from(Update update) {
        Objects.requireNonNull(update, "Update object can't be null!");

        final MessageType eventType;
        final Message message;

        if (update.hasMessage()) {
            eventType = MessageType.TEXT_MESSAGE;
            message = update.getMessage();
        } else if (update.hasEditedMessage()) {
            eventType = MessageType.TEXT_MESSAGE_EDIT;
            message = update.getEditedMessage();
        } else if (update.hasChannelPost()) {
            eventType = MessageType.TEXT_POST;
            message = update.getChannelPost();
        } else if (update.hasEditedChannelPost()) {
            eventType = MessageType.TEXT_POST_EDIT;
            message = update.getEditedChannelPost();
        } else {
            return null;
        }

        User fromUser = null;
        Long chatId = null;
        Chat chat = null;
        String text = null;

        if (message != null) {
            fromUser = message.getFrom();
            chatId = message.getChatId();
            chat = message.getChat();
            text = message.getText();
        }

        if (fromUser == null || chatId == null || text == null) {
            return null;
        }

        return new TelegramMessage(update, eventType, fromUser, chatId, chat, text);
    }

    public void setAttribute(String attributeName, Object value) {
        attributes.put(attributeName, value);
    }

    @Nullable
    public Object getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }
}
