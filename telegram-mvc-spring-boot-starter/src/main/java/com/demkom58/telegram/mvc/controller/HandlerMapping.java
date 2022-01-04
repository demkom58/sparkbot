package com.demkom58.telegram.mvc.controller;

import com.demkom58.telegram.mvc.message.MessageType;

public record HandlerMapping(MessageType[] messageTypes, String value) {
}
