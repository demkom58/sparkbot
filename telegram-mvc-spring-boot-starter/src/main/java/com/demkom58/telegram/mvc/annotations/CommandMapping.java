package com.demkom58.telegram.mvc.annotations;

import com.demkom58.telegram.mvc.message.MessageType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandMapping {
    String[] value() default {""};

    MessageType[] event() default {MessageType.TEXT_MESSAGE};
}
