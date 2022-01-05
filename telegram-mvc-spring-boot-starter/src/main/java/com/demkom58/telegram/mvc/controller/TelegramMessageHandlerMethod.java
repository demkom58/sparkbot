package com.demkom58.telegram.mvc.controller;

import com.demkom58.telegram.mvc.controller.argument.HandlerMethodArgumentResolverComposite;
import com.demkom58.telegram.mvc.message.TelegramMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
public class TelegramMessageHandlerMethod implements TelegramMessageHandler {
    private static final Object[] EMPTY_ARGS = new Object[0];

    private final HandlerMapping mapping;
    private final Object bean;
    private final Method method;
    private final Method protoMethod;

    private final MethodParameter[] parameters;
    private final MethodParameter returnType;

    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private HandlerMethodArgumentResolverComposite resolvers = new HandlerMethodArgumentResolverComposite();

    public TelegramMessageHandlerMethod(HandlerMapping mapping, Object bean, Method method) {
        this.mapping = mapping;
        this.bean = bean;
        this.method = method;
        this.protoMethod = BridgeMethodResolver.findBridgedMethod(method);
        ReflectionUtils.makeAccessible(protoMethod);
        this.parameters = methodParameters(method);
        this.returnType = new MethodParameter(method, -1);
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    public void setResolvers(HandlerMethodArgumentResolverComposite resolvers) {
        this.resolvers = resolvers;
    }

    @Nullable
    public Object invoke(TelegramMessage message, AbsSender bot, Object... providedArgs) throws Exception {
        Object[] args = getMethodArgumentValues(message, bot, providedArgs);
        if (log.isTraceEnabled()) {
            log.trace("Arguments: " + Arrays.toString(args));
        }

        return doInvoke(args);
    }

    protected Object[] getMethodArgumentValues(TelegramMessage message, AbsSender bot, Object... providedArgs) throws Exception {
        if (ObjectUtils.isEmpty(parameters)) {
            return EMPTY_ARGS;
        }

        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            args[i] = findProvidedArgument(parameter, providedArgs);
            if (args[i] != null) {
                continue;
            }

            if (!this.resolvers.isSupported(parameter)) {
                throw new IllegalStateException("Resolver supporting '" + parameter + "' not found!");
            }

            try {
                args[i] = this.resolvers.resolve(parameter, message, bot);
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    String exMsg = ex.getMessage();
                    if (exMsg != null && !exMsg.contains(parameter.getExecutable().toGenericString())) {
                        log.debug("An error occurred while resolving parameter {}", parameter, ex);
                    }
                }
                throw ex;
            }

        }
        return args;
    }

    @Nullable
    protected Object doInvoke(Object... args) throws Exception {
        try {
            return protoMethod.invoke(bean, args);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex.getMessage() != null ? ex.getMessage() : "Illegal argument", ex);
        } catch (InvocationTargetException ex) {
            Throwable targetEx = ex.getTargetException();
            if (targetEx instanceof RuntimeException exr) {
                throw exr;
            } else if (targetEx instanceof Error err) {
                throw err;
            } else if (targetEx instanceof Exception exr) {
                throw exr;
            } else {
                throw new IllegalStateException("Invocation failure", targetEx);
            }
        }
    }

    @Override
    public HandlerMapping getMapping() {
        return mapping;
    }

    public Method getMethod() {
        return method;
    }

    public Method getProtoMethod() {
        return protoMethod;
    }

    @Override
    public MethodParameter getReturnType() {
        return returnType;
    }

    private static MethodParameter[] methodParameters(Method method) {
        final int parameterCount = method.getParameterCount();
        final MethodParameter[] params = new MethodParameter[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            params[i] = new MethodParameter(method, i);
        }

        return params;
    }

    @Nullable
    protected static Object findProvidedArgument(MethodParameter parameter, @Nullable Object... providedArgs) {
        if (!ObjectUtils.isEmpty(providedArgs)) {
            for (Object providedArg : providedArgs) {
                if (parameter.getParameterType().isInstance(providedArg)) {
                    return providedArg;
                }
            }
        }
        return null;
    }

}
