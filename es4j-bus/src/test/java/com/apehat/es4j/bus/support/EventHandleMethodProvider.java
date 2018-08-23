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

package com.apehat.es4j.bus.support;

import static org.testng.Assert.assertEquals;

import com.apehat.es4j.bus.event.Event;
import java.lang.reflect.Method;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class EventHandleMethodProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHandleMethodProvider.class);

    private static final Method NORMAL_HANDLE_METHOD;
    private static final Method STATIC_HANDLE_METHOD;

    static {
        try {
            NORMAL_HANDLE_METHOD = EventHandleMethodProvider.class.getDeclaredMethod(
                "handleEvent", long.class, Event.class, String.class, Object.class);
            STATIC_HANDLE_METHOD = EventHandleMethodProvider.class.getDeclaredMethod(
                "staticEventHandler", long.class, Event.class, String.class, Object.class);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot find handler");
        }
    }

    private final Event expected;
    private boolean handled;

    public EventHandleMethodProvider() {
        this.expected = null;
    }

    public EventHandleMethodProvider(Event expected) {
        this.expected = Objects.requireNonNull(expected, "Expected must not be null.");
    }

    public static Method getStaticEventHandler() {
        return STATIC_HANDLE_METHOD;
    }

    public static String[] getHandlerParameterNames() {
        return new String[]{"occurredOn", "info", "source", "event"};
    }

    public Method getHandleMethod() {
        return NORMAL_HANDLE_METHOD;
    }

    public boolean isHandled() {
        return handled;
    }


    private static void staticEventHandler(
        long occurredOn, Event info, String source, Object event) {
        LOGGER.info("Start handler event with {}, {}", occurredOn, info);
    }

    private void handleEvent(long occurredOn, Event info, String source, Object event) {
        LOGGER.info("Start handler event occur on {}, {}", occurredOn, info);
        handled = true;
        if (expected != null) {
            assertEquals(expected, info);
            assertEquals(expected.occurredOn(), occurredOn);
            assertEquals(expected.source(), source);
            assertEquals(expected.prototype(), event);
        }
    }
}
