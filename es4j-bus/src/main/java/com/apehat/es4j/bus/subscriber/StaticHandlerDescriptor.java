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
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
final class StaticHandlerDescriptor implements HandlerDescriptor {

    private final Method handler;

    StaticHandlerDescriptor(Method handler) {
        if (!Modifier.isStatic(handler.getModifiers())) {
            throw new IllegalArgumentException(handler + "isn't static method.");
        }
        this.handler = Objects.requireNonNull(handler, "Handler must not be null");
    }

    @Override
    public EventHandler getHandler() {
        return new DynamicEventHandler(null, handler);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StaticHandlerDescriptor that = (StaticHandlerDescriptor) o;
        return Objects.equals(handler, that.handler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(handler);
    }
}
