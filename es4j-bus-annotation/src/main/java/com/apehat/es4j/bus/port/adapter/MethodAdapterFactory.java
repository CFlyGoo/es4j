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

package com.apehat.es4j.bus.port.adapter;

import com.apehat.es4j.bus.EventHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class MethodAdapterFactory implements EventHandlerAdapter {

    private final Object handler;
    private final Method handleMethod;

    public MethodAdapterFactory(Method staticHandler) {
        this(null, staticHandler);
    }

    public MethodAdapterFactory(Object handler, Method handleMethod) {
        this.handleMethod = Objects.requireNonNull(handleMethod, "Handle handler must not be null");
        if (!isStatic(handleMethod) && handler == null) {
            throw new IllegalArgumentException("Handler must not be null");
        }
        this.handler = handler;
    }

    private boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    @Override
    public EventHandler newEventHandler() {
        return isStatic(handleMethod) ?
            new StaticMethodAdapter(handleMethod) :
            new NormalMethodAdapter(handler, handleMethod);
    }
}
