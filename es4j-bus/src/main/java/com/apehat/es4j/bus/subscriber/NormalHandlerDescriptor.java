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

import com.apehat.es4j.bus.EventHandler;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
final class NormalHandlerDescriptor implements HandlerDescriptor {

    private final Object handler;
    private final Method handleMethod;

    NormalHandlerDescriptor(Object proxy, Method handleMethod) {
        this.handler = Objects.requireNonNull(proxy, "Handler must not be null");
        this.handleMethod = Objects.requireNonNull(handleMethod, "Handle method must not be null");
    }

    @Override
    public EventHandler getHandler() {
        return new DynamicEventHandler(handler, handleMethod);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NormalHandlerDescriptor that = (NormalHandlerDescriptor) o;
        return Objects.equals(handler, that.handler) &&
            Objects.equals(handleMethod, that.handleMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(handler, handleMethod);
    }
}
