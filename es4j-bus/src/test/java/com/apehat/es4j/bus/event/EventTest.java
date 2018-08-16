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

import static org.testng.Assert.assertEquals;

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
    private final Class<?> type;
    private final UserRegistered prototype;
    private final UserId userId;
    private final String username;
    private final Date registerOn;
    private final String userIdSource;

    public EventTest() {
        this.occurredOn = System.currentTimeMillis();
        this.source = UUID.randomUUID().toString();
        this.type = UserRegistered.class;
        this.userIdSource = UUID.randomUUID().toString();
        this.userId = new UserId(userIdSource);
        this.username = "testUsername";
        this.registerOn = new Date();
        this.prototype = new UserRegistered(userId, username, registerOn);
        EventPrototype eventPrototype = EventTestHelper.newPrototype(prototype);
        this.event = new Event(occurredOn, eventPrototype, source);
    }

    @Test
    public void testGet() {
        assertEquals(occurredOn, event.get(Event.OCCURRED_ON));
        assertEquals(prototype, event.get(Event.EVENT));
        assertEquals(type, event.get(Event.TYPE));
        assertEquals(source, event.get(Event.SOURCE));

        assertEquals(userId, event.get(UserRegistered.FIELD_USER_ID));
        assertEquals(userIdSource, event.get(UserRegistered.FIELD_USER_ID_ID));
        assertEquals(username, event.get(UserRegistered.FIELD_USERNAME));
        assertEquals(registerOn, event.get(UserRegistered.FIELD_REGISTER_ON));

        assertEquals(userId, event.get(Event.EVENT + "." + UserRegistered.FIELD_USER_ID));
        assertEquals(userIdSource, event.get(Event.EVENT + "." + UserRegistered.FIELD_USER_ID_ID));
        assertEquals(username, event.get(Event.EVENT + "." + UserRegistered.FIELD_USERNAME));
        assertEquals(registerOn, event.get(Event.EVENT + "." + UserRegistered.FIELD_REGISTER_ON));
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