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

package com.apehat.es4j.bus.subscriber;

import com.apehat.es4j.bus.Event;
import com.apehat.es4j.bus.EventHandler;
import com.apehat.es4j.bus.EventHandlingException;
import com.apehat.es4j.util.AsmParameterNameDiscoverer;
import com.apehat.es4j.util.ReflectionParameterNameDiscoverer;
import com.apehat.es4j.util.ReflectionUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
final class DynamicEventHandler implements EventHandler {

    private final Object proxy;
    private final Method handler;
    private transient volatile String[] cachedParameterNames;

    DynamicEventHandler(Object proxy, Method handler) {
        Objects.requireNonNull(handler, "Handle method must not be null");
        final int modifiers = handler.getModifiers();
        boolean isStatic = Modifier.isStatic(modifiers);
        if (!isStatic && proxy == null) {
            throw new IllegalArgumentException("Must specified proxy object for non static method");
        }
        ReflectionUtils.toAccessible(handler);
        this.handler = handler;
        this.proxy = proxy;
    }

    @Override
    public void onEvent(Event event) {
        final Object[] args = getArguments(event);
        try {
            handler.invoke(proxy, args);
        } catch (IllegalAccessException |
            InvocationTargetException e) {
            throw new EventHandlingException(e);
        }
    }

    private Object[] getArguments(Event event) {
        final int count = handler.getParameterCount();
        final Class<?>[] parameterTypes = handler.getParameterTypes();
        final Object[] args = new Object[count];
        for (int idx = 0; idx < count; idx++) {
            if (parameterTypes[idx] == Event.class) {
                args[idx] = event;
            } else {
                String name = getParameterNameAtIndex(idx);
                assert name != null;
                args[idx] = event.get(name);
            }
        }
        return args;
    }

    private String getParameterNameAtIndex(int index) {
        final int count = handler.getParameterCount();
        assert index < count;
        assert count > 0;
        if (cachedParameterNames != null) {
            return cachedParameterNames[index];
        }
        cachedParameterNames = new ReflectionParameterNameDiscoverer().getParameterNames(handler);
        if (cachedParameterNames == null) {
            cachedParameterNames = new AsmParameterNameDiscoverer().getParameterNames(handler);
        }
        assert cachedParameterNames != null;
        return cachedParameterNames[index];
    }
}
