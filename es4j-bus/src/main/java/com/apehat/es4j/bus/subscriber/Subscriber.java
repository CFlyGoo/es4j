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
import com.apehat.es4j.bus.PendingEvent;
import com.apehat.es4j.bus.Type;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class Subscriber {

    /*
     * if handler is no status, should identifier only by handler method and declared class
     * if handler is have status, should identifier by it's hash code and subscriber should in
     * memory only
     */

    private final EventHandler handler;
    private final long subscriptionOn;
    private final Type type;

    public Subscriber(EventHandler handler, Type type) {
        this.subscriptionOn = System.currentTimeMillis();
        this.handler = Objects.requireNonNull(handler, "Handler must not be null.");
        this.type = Objects.requireNonNull(type, "Subscription type must not be null.");
    }

    public void onEvent(PendingEvent event) {
        if (!type.equals(event.type())) {
            throw new IllegalArgumentException("Hadn't subscription to " + event.type());
        }
        if (subscriptionOn > event.occurredOn()) {
            throw new IllegalArgumentException("Event already occurred");
        }
        handler.onEvent(event.toEvent());
    }

    public Type subscriptionType() {
        return type;
    }

    public String id() {
        return handler.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Subscriber that = (Subscriber) o;
        return Objects.equals(handler, that.handler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(handler);
    }
}
