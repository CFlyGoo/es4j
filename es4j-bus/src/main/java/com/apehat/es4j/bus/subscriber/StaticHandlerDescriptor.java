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

import com.apehat.es4j.bus.DynamicEventHandler;
import com.apehat.es4j.bus.EventHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
class StaticHandlerDescriptor implements HandlerDescriptor {

    private final Method method;

    StaticHandlerDescriptor(Method method) {
        assert method != null;
        assert Modifier.isStatic(method.getModifiers());
        this.method = method;
    }

    @Override
    public EventHandler getHandler() {
        return new DynamicEventHandler(null, method);
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
        return Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method);
    }
}
