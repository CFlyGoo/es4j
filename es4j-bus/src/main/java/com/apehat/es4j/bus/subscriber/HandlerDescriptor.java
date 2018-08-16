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

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface HandlerDescriptor {

    static HandlerDescriptor of(Method handler) {
        return of(handler, null);
    }

    static HandlerDescriptor of(Method handleMethod, Object handler) {
        if (Modifier.isStatic(handleMethod.getModifiers())) {
            return new StaticHandlerDescriptor(handleMethod);
        }
        return new NormalHandlerDescriptor(handler, handleMethod);
    }

    static HandlerDescriptor of(EventHandler handler) {
        return new PlainHandleDescriptor(handler);
    }

    EventHandler getHandler();
}