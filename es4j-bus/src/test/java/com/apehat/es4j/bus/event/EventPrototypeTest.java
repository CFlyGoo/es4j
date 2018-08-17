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
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class EventPrototypeTest {

    private final UserRegistered metadata;
    private final EventPrototype prototype;

    public EventPrototypeTest() {
        this.metadata = userRegisteredFixture();
        this.metadata.getUserId().setPrototype(null);
        this.metadata.setUsername(null);
        this.prototype = new EventPrototype(this.metadata);
    }

    @Test
    public void testGetWithContainsNullFieldValueObject() {
        assertEquals(prototype.root(), metadata);
        assertEquals(metadata.getUserId(), prototype.get(USER_REGISTERED_ID));
        assertEquals(metadata.getUsername(), prototype.get(USER_REGISTERED_NAME));
        assertEquals(metadata.getRegisterOn(), prototype.get(USER_REGISTERED_TIME));
        assertEquals(metadata.getUserId().id(), prototype.get(USER_REGISTERED_ID_ID));
    }

    /**
     * When multiple get, that prototype should always find attribute name form cache
     * <p>
     * Note: for test cache
     */
    @Test
    public void testMultipleGetWithNullValue() {
        testGetWithContainsNullFieldValueObject();
        testGetWithContainsNullFieldValueObject();
    }
}