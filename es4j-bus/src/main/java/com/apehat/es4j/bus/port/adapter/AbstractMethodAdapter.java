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
import com.apehat.es4j.bus.EventHandlingException;
import com.apehat.es4j.bus.event.Event;
import com.apehat.es4j.util.ReflectionUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractMethodAdapter implements EventHandler {

    protected final Method handler;

    protected AbstractMethodAdapter(Method handler) {
        this.handler = Objects.requireNonNull(handler, "handle method must not be null");
        verify();
    }

    @Override
    public void onEvent(Event event) {
        ReflectionUtils.access(handler, accessible -> {
            try {
                return accessible.invoke(getInvoker(),
                    new EventHandlerArgumentsAssembler().getArguments(handler, event));
            } catch (InvocationTargetException e) {
                throw new EventHandlingException(e);
            }
        });
    }

    public final Method getHandler() {
        return handler;
    }

    protected void verify() {
    }

    protected abstract Object getInvoker();
}
