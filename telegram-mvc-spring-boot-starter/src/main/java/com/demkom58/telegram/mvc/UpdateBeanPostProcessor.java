package com.demkom58.telegram.mvc;

import com.demkom58.telegram.mvc.annotations.BotController;
import com.demkom58.telegram.mvc.annotations.CommandMapping;
import com.demkom58.telegram.mvc.config.PathMatchingConfigurer;
import com.demkom58.telegram.mvc.config.TelegramMvcConfigurerComposite;
import com.demkom58.telegram.mvc.controller.HandlerMapping;
import com.demkom58.telegram.mvc.controller.TelegramMessageHandlerMethod;
import com.demkom58.telegram.mvc.controller.argument.HandlerMethodArgumentResolver;
import com.demkom58.telegram.mvc.controller.argument.HandlerMethodArgumentResolverComposite;
import com.demkom58.telegram.mvc.controller.argument.impl.PathVariablesHandlerMethodArgumentResolver;
import com.demkom58.telegram.mvc.controller.result.HandlerMethodReturnValueHandler;
import com.demkom58.telegram.mvc.controller.result.HandlerMethodReturnValueHandlerComposite;
import com.demkom58.telegram.mvc.controller.result.impl.SendMessageHandlerMethodReturnValueHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

import java.lang.reflect.Method;
import java.util.*;

public class UpdateBeanPostProcessor implements BeanPostProcessor, Ordered {
    private final Map<String, Class<?>> botControllerMap = new HashMap<>();

    private final PathMatchingConfigurer pathMatchingConfigurer = new PathMatchingConfigurer();

    private final CommandContainer container;

    private final HandlerMethodArgumentResolverComposite argumentResolvers
            = new HandlerMethodArgumentResolverComposite();

    private final HandlerMethodReturnValueHandlerComposite returnValueHandlers
            = new HandlerMethodReturnValueHandlerComposite();

    public UpdateBeanPostProcessor(CommandContainer container, TelegramMvcConfigurerComposite configurerComposite) {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
        configurerComposite.configureArgumentResolvers(resolvers);
        resolvers.addAll(createArgumentResolvers());
        this.argumentResolvers.addAll(resolvers);

        List<HandlerMethodReturnValueHandler> returnHandlers = new ArrayList<>();
        configurerComposite.configureReturnValueHandlers(returnHandlers);
        returnHandlers.addAll(createReturnValueHandlers());
        this.returnValueHandlers.addAll(returnHandlers);

        this.container = container;
        configurerComposite.configurePathMatcher(this.pathMatchingConfigurer);
        container.setPathMatchingConfigurer(this.pathMatchingConfigurer);
        container.setReturnValueHandlers(this.returnValueHandlers);
    }

    private List<HandlerMethodArgumentResolver> createArgumentResolvers() {
        return Arrays.asList(
                new PathVariablesHandlerMethodArgumentResolver()
        );
    }

    private List<HandlerMethodReturnValueHandler> createReturnValueHandlers() {
        return Arrays.asList(
                new SendMessageHandlerMethodReturnValueHandler()
        );
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean,
                                                  @NonNull String beanName) throws BeansException {
        final Class<?> beanClass = bean.getClass();

        if (beanClass.isAnnotationPresent(BotController.class))
            botControllerMap.put(beanName, beanClass);

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean,
                                                 @NonNull String beanName) throws BeansException {
        final Class<?> original = botControllerMap.get(beanName);
        if (original == null)
            return bean;

        Arrays.stream(original.getMethods())
                .filter(method -> method.isAnnotationPresent(CommandMapping.class))
                .forEach((Method method) -> generateController(bean, method));

        return bean;
    }

    private void generateController(Object bean, Method method) {
        final BotController botController = bean.getClass().getAnnotation(BotController.class);
        final CommandMapping mapping = method.getAnnotation(CommandMapping.class);

        final Set<String> paths = new HashSet<>();
        final String[] controllerValues = botController.value().length != 0 ? botController.value() : new String[]{""};
        final String[] mappingValues = mapping.value().length != 0 ? mapping.value() : new String[]{""};

        for (String headPath : controllerValues) {
            for (String mappedPath : mappingValues) {
                paths.add(headPath.toLowerCase() + mappedPath.toLowerCase());
            }
        }

        for (String path : paths) {
            final var handlerMapping = new HandlerMapping(mapping.event(), path);
            final var handlerMethod = new TelegramMessageHandlerMethod(handlerMapping, bean, method);
            handlerMethod.setResolvers(argumentResolvers);
            container.addBotController(path, handlerMethod);
        }

    }

    @Override
    public int getOrder() {
        return 100;
    }

}