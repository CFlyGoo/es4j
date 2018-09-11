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

import com.apehat.argument.binding.alias.AsmParameterAliasDiscoverer;
import com.apehat.argument.binding.alias.PrioritizedParameterAliasDiscoverer;
import com.apehat.argument.binding.alias.ReflectionParameterAliasDiscoverer;
import com.apehat.argument.binding.ArgumentsAssembler;
import com.apehat.argument.binding.DefaultArgumentsAssembler;
import com.apehat.es4j.bus.EventHandler;
import com.apehat.es4j.bus.EventHandlingException;
import com.apehat.es4j.bus.annotation.AnnotatedParameterAliasDiscoverer;
import com.apehat.es4j.bus.event.Event;
import com.apehat.util.ReflectionUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public abstract class AbstractMethodAdapter implements EventHandler {

    private static final ArgumentsAssembler<Event> ASSEMBLER;

    static {
        PrioritizedParameterAliasDiscoverer discoverer = new PrioritizedParameterAliasDiscoverer();
        discoverer.registerDiscoverer(new AnnotatedParameterAliasDiscoverer());
        discoverer.registerDiscoverer(new ReflectionParameterAliasDiscoverer());
        discoverer.registerDiscoverer(new AsmParameterAliasDiscoverer());
        ASSEMBLER = new DefaultArgumentsAssembler<>(discoverer, new EventArgumentAdapter());
    }

    private final Method handler;

    protected AbstractMethodAdapter(Method handler) {
        this.handler = Objects.requireNonNull(handler, "handle method must not be null");
    }

    @Override
    public void onEvent(Event event) {
        ReflectionUtils.access(handler, accessible -> {
            try {
                return accessible.invoke(
                    getInvoker(), ASSEMBLER.assemble(handler, event));
            } catch (InvocationTargetException e) {
                throw new EventHandlingException(e);
            }
        });
    }

    public final Method getHandler() {
        return handler;
    }

    protected abstract Object getInvoker();
}
