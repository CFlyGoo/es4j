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
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
@SuppressWarnings("WeakerAccess")
public final class EventBus {

    /* Global subscribe */

    public String register(EventHandler handler) {
        return subscribe(Object.class, handler);
    }

    public String register(Object subscriber, Method handler) {
        return subscribe(Object.class, subscriber, handler);
    }

    /* Type specified subscribe */

    public String subscribe(Class<?> type, EventHandler handler) {
        return subscribe(Type.of(type), handler);
    }

    public String subscribe(Class<?> type, Object subscriber, Method handler) {
        return subscribe(Type.of(type), subscriber, handler);
    }

    public String subscribe(Type type, EventHandler handler) {
        throw new NotImplementedException();
    }

    public String subscribe(Type type, Object subscriber, Method handler) {
        DynamicEventHandler dynamicEventHandler = new DynamicEventHandler(subscriber, handler);
        return subscribe(type, dynamicEventHandler);
    }

    /* Publish */

    public void publish(Object event) {
        publish(Type.of(event.getClass()), event);
    }

    public void publish(Type type, Object event) {
        throw new NotImplementedException();
    }

    public void publish(Object event, Callback callback) {
        publish(Type.of(event.getClass()), event, callback);
    }

    public void publish(Type type, Object event, Callback callback) {
        throw new NotImplementedException();
    }

    /* Submit */

    public void submit(Object event) {
        submit(Type.of(event.getClass()), event);
    }

    public void submit(Type type, Object event) {
        throw new NotImplementedException();
    }

    public void submit(Object event, Callback callback) {
        submit(Type.of(event.getClass()), event, callback);
    }

    public void submit(Type type, Object event, Callback callback) {
        throw new NotImplementedException();
    }

    /* Query */

    public Set<String> allGlobalSubscribers() {
        return subscribersOf(Object.class);
    }

    public Set<String> subscribersOf(Class<?> type) {
        return subscribersOf(Type.of(type));
    }

    public Set<String> subscribersOf(Type type) {
        throw new NotImplementedException();
    }
}
