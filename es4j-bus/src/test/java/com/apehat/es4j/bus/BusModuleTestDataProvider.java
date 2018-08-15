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

import com.apehat.es4j.bus.event.EventPrototype;
import com.apehat.es4j.bus.event.EventTestHelper;
import com.apehat.es4j.bus.support.UserId;
import com.apehat.es4j.bus.support.UserRegistered;
import java.util.Date;
import java.util.UUID;
import org.testng.annotations.DataProvider;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class BusModuleTestDataProvider {

    @DataProvider
    public Object[] eventDataProvider() {
        Object[] result = new Object[1];
        result[0] = getEvent();
        return result;
    }

    protected Event getEvent() {
        long occurredOn = System.currentTimeMillis();
        String source = UUID.randomUUID().toString();
        Type type = Type.of(UserRegistered.class);
        UserId userId = new UserId(UUID.randomUUID().toString());
        String username = "testUsername";
        Date registerOn = new Date();
        UserRegistered prototype = new UserRegistered(userId, username, registerOn);
        EventPrototype eventPrototype = EventTestHelper.newPrototype(prototype);
        return new Event(occurredOn, eventPrototype, type, source);
    }
}
