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

import static org.testng.Assert.assertEquals;

import com.apehat.es4j.bus.event.EventPrototype;
import com.apehat.es4j.bus.event.EventTestHelper;
import com.apehat.es4j.bus.support.UserId;
import com.apehat.es4j.bus.support.UserRegistered;
import java.util.Date;
import java.util.UUID;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class EventTest {

    private final long occurredOn;
    private final String source;
    private final Event event;
    private final Type type;
    private final UserRegistered prototype;
    private final UserId userId;
    private final String username;
    private final Date registerOn;

    public EventTest() {
        occurredOn = System.currentTimeMillis();
        source = UUID.randomUUID().toString();
        type = Type.of(UserRegistered.class);
        userId = new UserId(UUID.randomUUID().toString());
        username = "testUsername";
        registerOn = new Date();
        prototype = new UserRegistered(userId, username, registerOn);
        EventPrototype eventPrototype = EventTestHelper.newPrototype(prototype);
        event = new Event(occurredOn, eventPrototype, type, source);
    }

    @Test
    public void testGet() {
        assertEquals(occurredOn, event.get(Event.OCCURRED_ON));
        assertEquals(prototype, event.get(Event.EVENT));
        assertEquals(type, event.get(Event.TYPE));
        assertEquals(source, event.get(Event.SOURCE));

        assertEquals(userId, event.get("userId"));
        assertEquals(username, event.get("username"));
        assertEquals(registerOn, event.get("registerOn"));
        assertEquals(userId, event.get("event.userId"));
        assertEquals(username, event.get("event.username"));
        assertEquals(registerOn, event.get("event.registerOn"));
    }

    @Test
    public void testOccurredOn() {
        assertEquals(occurredOn, event.occurredOn());
    }

    @Test
    public void testPrototype() {
        assertEquals(prototype, event.prototype());
    }

    @Test
    public void testSource() {
        assertEquals(source, event.source());
    }

    @Test
    public void testType() {
        assertEquals(type, event.type());
    }
}