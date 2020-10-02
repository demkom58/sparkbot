package com.demkom58.spark.bot;

import com.demkom58.spark.service.PaymentService;
import com.demkom58.spark.service.UserService;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@Getter
public class SparkBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(SparkBot.class);

    private final UserService userService;
    private final PaymentService paymentService;

    private final String botToken;
    private final String botUsername;

    private final ReplyKeyboardMarkup menuKeyboardMarkup = new ReplyKeyboardMarkup();

    public SparkBot(final UserService userService,
                    final PaymentService paymentService,
                    @Value("${bot.token}") String token,
                    @Value("${bot.username}") String username) {
        this.userService = userService;
        this.paymentService = paymentService;
        this.botToken = token;
        this.botUsername = username;

        final var read = new KeyboardRow();
        read.addAll(List.of(
                "Группы",
                "Заявки"
        ));

        final var write = new KeyboardRow();
        write.addAll(List.of(
                "Отправить",
                "Управление"
        ));

        final var other = new KeyboardRow();
        other.addAll(List.of(
                "Создать группу",
                "Донат"
        ));

        menuKeyboardMarkup.setSelective(true);
        menuKeyboardMarkup.setResizeKeyboard(true);
        menuKeyboardMarkup.setOneTimeKeyboard(false);
        menuKeyboardMarkup.setKeyboard(List.of(read, write, other));
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage())
            return;

        final var message = update.getMessage();
        if (message == null)
            return;

        final var text = message.getText();
        if (text == null)
            return;

        var chat = message.getChat();
        var tgUser = message.getFrom();
        if (!chat.isUserChat() || tgUser == null || tgUser.getBot())
            return;

        switch (text) {
            case "/start" -> start(chat, tgUser, message);
            default -> unknown(chat);
        }

    }

    private void start(@NotNull final Chat chat,
                       @NotNull final User tgUser,
                       @NotNull final Message message) {
        final var response = new SendMessage()
                .setChatId(chat.getId())
                .setText("Отобразил тебе меню :)")
                .setReplyMarkup(menuKeyboardMarkup);

        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void unknown(@NotNull final Chat chat) {
        try {
            execute(
                    new SendMessage()
                            .setChatId(chat.getId())
                            .setText("Я не знаю такой команды :(")
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
