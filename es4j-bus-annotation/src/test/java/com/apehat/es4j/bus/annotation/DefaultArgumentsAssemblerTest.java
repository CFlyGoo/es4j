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

package com.apehat.es4j.bus.annotation;

import static org.testng.Assert.assertEquals;

import com.apehat.argument.binding.ArgumentsAssembler;
import com.apehat.argument.binding.DefaultArgumentsAssembler;
import com.apehat.argument.binding.alias.DefaultParameterAliasDiscoverer;
import com.apehat.es4j.bus.event.Event;
import com.apehat.es4j.bus.event.EventFixtureProvider;
import com.apehat.es4j.bus.port.adapter.EventArgumentAdapterProvider;
import com.apehat.support.TestDataProvider;
import com.apehat.support.UserId;
import com.apehat.support.UserRegistered;
import java.lang.reflect.Method;
import java.util.Date;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class DefaultArgumentsAssemblerTest {

    private final Event event;
    private final UserRegistered prototype;
    private final ArgumentsAssembler<Event> assembler;

    public DefaultArgumentsAssemblerTest() {
        this.prototype = TestDataProvider.userRegisteredFixture();
        this.event = EventFixtureProvider.newEventFixture(this.prototype);
        this.assembler = new DefaultArgumentsAssembler<>(
            new DefaultParameterAliasDiscoverer(),
            EventArgumentAdapterProvider.eventArgumentAdapterFixture());
    }

    @Test
    public void testAssemble() throws Exception {
        Method method = getClass().getDeclaredMethod(
            "testGetSingleLayerArguments",
            long.class, Object.class, Class.class,
            String.class, UserId.class, String.class, Date.class);
        Object[] arguments = assembler.assemble(method, event);
        //noinspection JavaReflectionInvocation
        method.invoke(this, arguments);
    }

    private void testGetSingleLayerArguments(long occurredOn, Object event,
        Class<?> type, String source, UserId userId, String username, Date registerOn) {
        assertEquals(this.event.occurredOn(), occurredOn);
        assertEquals(this.event.prototype(), event);
        assertEquals(this.event.type(), type);
        assertEquals(this.event.source(), source);
        assertEquals(prototype.getUserId(), userId);
        assertEquals(prototype.getUsername(), username);
        assertEquals(prototype.getRegisterOn(), registerOn);
    }
}
