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
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class EventPrototypeTest {

    private final String userIdSource = null;
    private final UserId userId = new UserId(userIdSource);
    private final Date userRegisterDate = new Date();
    private final String username = null;
    private final UserRegistered metadata = new UserRegistered(userId, username, userRegisterDate);
    private final EventPrototype prototype = new EventPrototype(metadata);

    @Test
    public void testGetWithContainsNullFieldValueObject() {
        assertEquals(prototype.root(), metadata);
        assertEquals(userId, prototype.get(UserRegistered.FIELD_USER_ID));
        assertEquals(username, prototype.get(UserRegistered.FIELD_USERNAME));
        assertEquals(userRegisterDate, prototype.get(UserRegistered.FIELD_REGISTER_ON));
        assertEquals(userIdSource, prototype.get(UserRegistered.FIELD_USER_ID_ID));
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