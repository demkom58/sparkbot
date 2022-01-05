package com.demkom58.telegram.mvc.config;

import com.demkom58.telegram.mvc.controller.argument.HandlerMethodArgumentResolver;
import com.demkom58.telegram.mvc.controller.result.HandlerMethodReturnValueHandler;

import java.util.List;

public interface TelegramMvcConfigurer {

    default void configurePathMatcher(PathMatchingConfigurer configurer) {

    }

    default void configureArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolverList) {

    }

    default void configureReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {

    }

}
