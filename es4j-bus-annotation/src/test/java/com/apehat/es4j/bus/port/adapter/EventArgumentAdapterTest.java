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

import static com.apehat.support.TestDataProvider.USER_REGISTERED_ID;
import static com.apehat.support.TestDataProvider.USER_REGISTERED_ID_ID;
import static com.apehat.support.TestDataProvider.USER_REGISTERED_NAME;
import static com.apehat.support.TestDataProvider.USER_REGISTERED_TIME;
import static org.testng.Assert.assertEquals;

import com.apehat.es4j.bus.event.Event;
import com.apehat.es4j.bus.event.EventFixtureProvider;
import com.apehat.Value;
import com.apehat.support.TestDataProvider;
import com.apehat.support.UserRegistered;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class EventArgumentAdapterTest {

    private final Event event;
    private final UserRegistered prototype;
    private final EventArgumentAdapter extractor;

    public EventArgumentAdapterTest() {
        this.prototype = TestDataProvider.userRegisteredFixture();
        this.event = EventFixtureProvider.newEventFixture(prototype);
        this.extractor = new EventArgumentAdapter();
    }

    @Test
    public void testAdapt() {
        assertEquals(event.occurredOn(), adapt(Event.OCCURRED_ON));
        assertEquals(event.prototype(), adapt(Event.EVENT));
        assertEquals(event.type(), adapt(Event.TYPE));
        assertEquals(event.source(), adapt(Event.SOURCE));

        assertEquals(prototype.getUserId(), adapt(USER_REGISTERED_ID));
        assertEquals(prototype.getUserId().getId(), adapt(USER_REGISTERED_ID_ID));
        assertEquals(prototype.getUsername(), adapt(USER_REGISTERED_NAME));
        assertEquals(prototype.getRegisterOn(), adapt(USER_REGISTERED_TIME));

        assertEquals(prototype.getUserId(),
            adapt(Event.EVENT + "." + USER_REGISTERED_ID));
        assertEquals(prototype.getUserId().getId(),
            adapt(Event.EVENT + "." + USER_REGISTERED_ID_ID));
        assertEquals(prototype.getUsername(),
            adapt(Event.EVENT + "." + USER_REGISTERED_NAME));
        assertEquals(prototype.getRegisterOn(),
            adapt(Event.EVENT + "." + USER_REGISTERED_TIME));
    }

    private Object adapt(String name) {
        Value<?> value = extractor.adapt(name, event);
        if (value == null) {
            return null;
        }
        return value.get();
    }
}