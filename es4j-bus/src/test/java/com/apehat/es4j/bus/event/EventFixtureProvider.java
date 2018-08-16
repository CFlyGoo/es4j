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

import com.apehat.es4j.bus.support.UserId;
import com.apehat.es4j.bus.support.UserRegistered;
import java.util.Date;
import java.util.UUID;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class EventFixtureProvider {

    public static Event newEventFixture() {
        long occurredOn = System.currentTimeMillis();
        String source = UUID.randomUUID().toString();
        UserId userId = new UserId(UUID.randomUUID().toString());
        String username = "testUsername";
        Date registerOn = new Date();
        UserRegistered prototype = new UserRegistered(userId, username, registerOn);
        EventPrototype eventPrototype = EventTestHelper.newPrototype(prototype);
        return new Event(occurredOn, eventPrototype, source);
    }
}
