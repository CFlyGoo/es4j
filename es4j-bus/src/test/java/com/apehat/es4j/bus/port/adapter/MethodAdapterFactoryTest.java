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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import com.apehat.es4j.bus.support.EventHandleMethodProvider;
import java.lang.reflect.Method;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class MethodAdapterFactoryTest {

    @Test
    public void testCreateNewEventHandlerWithSpecifiedInvokerAndOnlySpecifyStaticMethodIsEquals() {
        EventHandleMethodProvider invoker = new EventHandleMethodProvider();
        assertEquals(
            new MethodAdapterFactory(invoker, EventHandleMethodProvider.getStaticEventHandler())
                .newEventHandler(),
            new MethodAdapterFactory(EventHandleMethodProvider.getStaticEventHandler())
                .newEventHandler());
    }

    @Test
    public void testCreateNewEventHandlerWithDifferenceInvokerAndSameNormalMethodNotEquals() {
        final Method handleMethod = new EventHandleMethodProvider().getHandleMethod();
        assertNotEquals(
            new MethodAdapterFactory(new EventHandleMethodProvider(), handleMethod)
                .newEventHandler(),
            new MethodAdapterFactory(new EventHandleMethodProvider(), handleMethod)
                .newEventHandler());
    }

    @Test
    public void testCreateNewEventHandlerWithStaticMethodAndNullInvoker() {
        new MethodAdapterFactory(null, EventHandleMethodProvider.getStaticEventHandler())
            .newEventHandler();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructAdapterFactoryWithNullMethod() {
        new MethodAdapterFactory(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructAdapterFactoryWithSpecifiedInvokerAndNullMethod() {
        new MethodAdapterFactory(new EventHandleMethodProvider(), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructAdapterFactoryWithNormalMethodAndNullInvoker() {
        new MethodAdapterFactory(null, new EventHandleMethodProvider().getHandleMethod());
    }
}