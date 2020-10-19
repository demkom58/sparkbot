package com.demkom58.spark.controller;

import com.demkom58.spark.entity.GroupAccess;
import com.demkom58.spark.entity.User;
import com.demkom58.spark.mvc.EventType;
import com.demkom58.spark.mvc.annotations.BotController;
import com.demkom58.spark.mvc.annotations.CommandMapping;
import com.demkom58.spark.repo.GroupAccessRepository;
import com.demkom58.spark.service.UserService;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;
import java.util.StringJoiner;

@BotController
@RequiredArgsConstructor
public class GroupController {
    private final GroupAccessRepository groupAccessRepository;
    private final UserService userService;

    @CommandMapping(
            value = {"/groups", "Groups", "Группы"},
            event = EventType.TEXT_MESSAGE
    )
    public SendMessage groups(Update update) {
        final var message = update.getMessage();
        final var tgUser = message.getFrom();

        final Long chatId = message.getChatId();
        final Integer authorId = tgUser.getId();

        final User user = userService.getUser(authorId);
        final Collection<GroupAccess> accesses = groupAccessRepository.getAllByUser(user);
        if (accesses.isEmpty())
            return new SendMessage()
                    .setChatId(chatId)
                    .setText("У тебя нет доступа ни к каким группам :(");

        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("Ты состоишь в группах:");
        accesses.forEach(access -> {
            var group = access.getGroup();
            joiner.add(group.getName() + "(#" + group.getId() + ")");
        });

        return new SendMessage()
                .setChatId(chatId)
                .setText(joiner.toString());
    }
}