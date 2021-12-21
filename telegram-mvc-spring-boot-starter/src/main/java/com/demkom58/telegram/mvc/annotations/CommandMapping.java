package com.demkom58.telegram.mvc.annotations;

import com.demkom58.telegram.mvc.EventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandMapping {
    String[] value() default {};

    EventType[] event() default {EventType.TEXT_MESSAGE};

    boolean pathOnly() default false;
}
