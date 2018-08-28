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

import static com.apehat.es4j.support.TestDataProvider.USER_REGISTERED_ID;
import static com.apehat.es4j.support.TestDataProvider.USER_REGISTERED_ID_ID;
import static com.apehat.es4j.support.TestDataProvider.USER_REGISTERED_NAME;
import static com.apehat.es4j.support.TestDataProvider.USER_REGISTERED_TIME;
import static org.testng.Assert.assertEquals;

import com.apehat.es4j.bus.event.Event;
import com.apehat.es4j.bus.event.EventFixtureProvider;
import com.apehat.es4j.support.TestDataProvider;
import com.apehat.es4j.support.UserRegistered;
import com.apehat.es4j.util.Value;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class EventArgumentExtractorTest {

    @Test
    public void testExtract() {
        assertEquals(event.occurredOn(), extract(Event.OCCURRED_ON));
        assertEquals(event.prototype(), extract(Event.EVENT));
        assertEquals(event.type(), extract(Event.TYPE));
        assertEquals(event.source(), extract(Event.SOURCE));

        assertEquals(prototype.getUserId(), extract(USER_REGISTERED_ID));
        assertEquals(prototype.getUserId().getId(), extract(USER_REGISTERED_ID_ID));
        assertEquals(prototype.getUsername(), extract(USER_REGISTERED_NAME));
        assertEquals(prototype.getRegisterOn(), extract(USER_REGISTERED_TIME));

        assertEquals(prototype.getUserId(),
            extract(Event.EVENT + "." + USER_REGISTERED_ID));
        assertEquals(prototype.getUserId().getId(),
            extract(Event.EVENT + "." + USER_REGISTERED_ID_ID));
        assertEquals(prototype.getUsername(),
            extract(Event.EVENT + "." + USER_REGISTERED_NAME));
        assertEquals(prototype.getRegisterOn(),
            extract(Event.EVENT + "." + USER_REGISTERED_TIME));
    }

    private final Event event;
    private final UserRegistered prototype;
    private final EventArgumentExtractor extractor;

    public EventArgumentExtractorTest() {
        this.prototype = TestDataProvider.userRegisteredFixture();
        this.event = EventFixtureProvider.newEventFixture(prototype);
        this.extractor = new EventArgumentExtractor();
    }

    private Object extract(String name) {
        Value<?> value = extractor.extract(name, event);
        if (value == null) {
            return null;
        }
        return value.get();
    }
}