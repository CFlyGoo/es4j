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

import com.apehat.es4j.bus.DomainRegistry;
import com.apehat.es4j.bus.event.Event;
import java.lang.reflect.Method;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class EventHandlerArgumentsAssembler {

    public Object[] getArguments(Method handler, Event event) {
        final int count = handler.getParameterCount();
        final Class<?>[] parameterTypes = handler.getParameterTypes();
        final Object[] args = new Object[count];
        for (int idx = 0; idx < count; idx++) {
            if (parameterTypes[idx] == Event.class) {
                args[idx] = event;
            } else {
                String name = DomainRegistry.parameterNameDiscoverer()
                    .getParameterName(handler, idx);
                assert name != null;
                args[idx] = event.get(name);
            }
        }
        return args;
    }
}
