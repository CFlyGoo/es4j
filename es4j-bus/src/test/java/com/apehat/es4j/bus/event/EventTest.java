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

package com.apehat.es4j.bus.event;

import static com.apehat.es4j.support.TestDataProvider.USER_REGISTERED_ID;
import static com.apehat.es4j.support.TestDataProvider.USER_REGISTERED_ID_ID;
import static com.apehat.es4j.support.TestDataProvider.USER_REGISTERED_NAME;
import static com.apehat.es4j.support.TestDataProvider.USER_REGISTERED_TIME;
import static com.apehat.es4j.support.TestDataProvider.userRegisteredFixture;
import static org.testng.Assert.assertEquals;

import com.apehat.es4j.support.UserRegistered;
import java.util.UUID;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class EventTest {

    private final Event event;
    private final UserRegistered prototype;

    public EventTest() {
        long occurredOn = System.currentTimeMillis();
        String source = UUID.randomUUID().toString();
        this.prototype = userRegisteredFixture();
        EventPrototype eventPrototype = new EventPrototype(prototype);
        this.event = new Event(occurredOn, eventPrototype, source);
    }

    @Test
    public void testGet() {
        assertEquals(event.occurredOn(), event.get(Event.OCCURRED_ON));
        assertEquals(event.prototype(), event.get(Event.EVENT));
        assertEquals(event.type(), event.get(Event.TYPE));
        assertEquals(event.source(), event.get(Event.SOURCE));

        assertEquals(prototype.getUserId(), event.get(USER_REGISTERED_ID));
        assertEquals(prototype.getUserId().id(), event.get(USER_REGISTERED_ID_ID));
        assertEquals(prototype.getUsername(), event.get(USER_REGISTERED_NAME));
        assertEquals(prototype.getRegisterOn(), event.get(USER_REGISTERED_TIME));

        assertEquals(prototype.getUserId(), event.get(Event.EVENT + "." + USER_REGISTERED_ID));
        assertEquals(prototype.getUserId().id(),
            event.get(Event.EVENT + "." + USER_REGISTERED_ID_ID));
        assertEquals(prototype.getUsername(), event.get(Event.EVENT + "." + USER_REGISTERED_NAME));
        assertEquals(prototype.getRegisterOn(),
            event.get(Event.EVENT + "." + USER_REGISTERED_TIME));
    }
}