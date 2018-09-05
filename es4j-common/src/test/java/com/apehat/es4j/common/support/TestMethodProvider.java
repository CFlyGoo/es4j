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

package com.apehat.es4j.common.support;

import static org.testng.AssertJUnit.assertEquals;

import java.lang.reflect.Method;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class TestMethodProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestMethodProvider.class);

    private static final Method NORMAL_HANDLE_METHOD;

    static {
        try {
            NORMAL_HANDLE_METHOD = TestMethodProvider.class.getDeclaredMethod(
                "handleEvent", long.class, Event.class, String.class, Object.class);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot find handler");
        }
    }

    private final Event expected;

    public TestMethodProvider() {
        this.expected = null;
    }

    public static String[] getHandlerParameterNames() {
        return new String[]{"occurredOn", "info", "source", "event"};
    }

    public Method getHandleMethod() {
        return NORMAL_HANDLE_METHOD;
    }

    private void handleEvent(long occurredOn, Event info, String source, Object event) {
        LOGGER.info("Start handler event occur on {}, {}", occurredOn, info);
        if (expected != null) {
            assertEquals(expected, info);
            assertEquals(expected.occurredOn(), occurredOn);
            assertEquals(expected.source(), source);
            assertEquals(expected.prototype(), event);
        }
    }

    private static class Event {

        long occurredOn() {
            return 0;
        }

        String source() {
            return null;
        }

        Object prototype() {
            return null;
        }
    }
}
