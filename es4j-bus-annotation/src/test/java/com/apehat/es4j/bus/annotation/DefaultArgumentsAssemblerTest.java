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

import com.apehat.es4j.common.Value;
import com.apehat.es4j.common.alias.DefaultParameterAliasDiscoverer;
import com.apehat.es4j.common.argument.ArgumentsAssembler;
import com.apehat.es4j.common.argument.DefaultArgumentsAssembler;
import com.apehat.es4j.common.argument.support.DefaultArgumentAdapter;
import com.apehat.es4j.common.argument.support.PrioritizedArgumentAdapter;
import com.apehat.es4j.common.util.ObjectUtils;
import com.apehat.es4j.support.TestDataProvider;
import com.apehat.es4j.support.UserId;
import com.apehat.es4j.support.UserRegistered;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
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
        this.event = new Event(
            System.currentTimeMillis(),
            this.prototype,
            UUID.randomUUID().toString());
        this.assembler = new DefaultArgumentsAssembler<>(
            new DefaultParameterAliasDiscoverer(),
            new EventArgumentAdapter());
    }

    private final class EventArgumentAdapter extends PrioritizedArgumentAdapter {

        EventArgumentAdapter() {
            registerAdapter(new AnnotatedMethodAdapter());
            registerAdapter(new AnnotateFieldAdapter());
            registerAdapter(new DefaultArgumentAdapter());
        }

        @Override
        public Value<?> adapt(String alias, Object prototype) {
            Value<?> value = super.adapt(alias, prototype);
            if (value != null) {
                return value;
            }
            return super.adapt(alias, ((Event) prototype).prototype());
        }
    }

    @Test
    public void testAssemble() throws Exception {
        Method method = DefaultArgumentsAssemblerTest.class
            .getDeclaredMethod("testGetSingleLayerArguments",
                long.class, Object.class, Class.class,
                String.class, UserId.class, String.class, Date.class);
        Object[] arguments = assembler.assemble(method, event);
        //noinspection JavaReflectionInvocation
        method.invoke(this, arguments);
    }

    private void testGetSingleLayerArguments(
        long occurredOn, Object event, Class<?> type,
        String source, UserId userId, String username, Date registerOn) {
        assertEquals(this.event.occurredOn(), occurredOn);
        assertEquals(this.event.prototype(), event);
        assertEquals(this.event.type(), type);
        assertEquals(this.event.source(), source);
        assertEquals(prototype.getUserId(), userId);
        assertEquals(prototype.getUsername(), username);
        assertEquals(prototype.getRegisterOn(), registerOn);
    }

    public final class Event {

        private final long occurredOn;
        private final Object prototype;
        private final String source;

        Event(long occurredOn, Object prototype, String source) {
            this.prototype = ObjectUtils.deepClone(
                Objects.requireNonNull(prototype, "Event prototype must not be null"));
            this.occurredOn = occurredOn;
            this.source = source;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Event that = (Event) o;
            return occurredOn == that.occurredOn &&
                Objects.equals(prototype, that.prototype) &&
                Objects.equals(source, that.source);
        }

        @Override
        public int hashCode() {
            int result = 253;
            result = 31 * result + Objects.hash(occurredOn);
            result = 31 * result + Objects.hash(prototype);
            result = 31 * result + Objects.hash(source);
            return result;
        }

        @Override
        public String toString() {
            return "Event{" +
                "type=" + type() +
                ", occurredOn=" + occurredOn +
                ", prototype=" + prototype +
                ", source='" + source + '\'' +
                '}';
        }

        long occurredOn() {
            return occurredOn;
        }

        @Alias("event")
        Object prototype() {
            return ObjectUtils.deepClone(prototype);
        }

        String source() {
            return source;
        }

        Class<?> type() {
            return prototype.getClass();
        }
    }
}