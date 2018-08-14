/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apehat.es4j.bus;

import com.apehat.es4j.NotImplementedException;
import com.apehat.es4j.util.ObjectUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class DynamicEventHandler implements EventHandler {

    private final Object proxy;
    private final Method handler;

    public DynamicEventHandler(Object proxy, Method handler) {
        Objects.requireNonNull(handler, "Handle method must not be null");
        final int modifiers = handler.getModifiers();
        boolean isStatic = Modifier.isStatic(modifiers);
        if (!isStatic && proxy == null) {
            throw new IllegalArgumentException("Must specified proxy object for non static method");
        }
        ObjectUtils.toAccessible(handler);
        this.handler = handler;
        this.proxy = proxy;
    }

    @Override
    public void onEvent(Event event) {
        final int count = handler.getParameterCount();
        Parameter[] parameters = handler.getParameters();
        final Object[] args = new Object[count];
        for (int idx = 0; idx < count; idx++) {
            final Parameter parameter = parameters[idx];
            final Class<?> parameterType = parameter.getType();
            final Object arg;
            if (parameterType == Event.class) {
                arg = event;
            } else {
                String name;
                if (parameter.isNamePresent()) {
                    name = parameter.getName();
                } else {
                    // TODO get by ASM
                    throw new NotImplementedException();
                }
                arg = event.get(name);
            }
            args[idx] = arg;
        }
        try {
            handler.invoke(proxy, args);
        } catch (IllegalAccessException |
            InvocationTargetException e) {
            throw new EventHandlingException(e);
        }
    }
}
