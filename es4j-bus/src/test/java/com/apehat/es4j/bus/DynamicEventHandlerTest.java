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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.apehat.es4j.bus.support.MockDynamicEventHandler;
import java.lang.reflect.Method;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class DynamicEventHandlerTest {

    @Test(dataProviderClass = BusModuleTestDataProvider.class, dataProvider = "eventDataProvider")
    public void testOnEvent(Event event) {
        final MockDynamicEventHandler proxy = new MockDynamicEventHandler(event);
        final Method handler = MockDynamicEventHandler.getEventHandler();
        DynamicEventHandler dynamicEventHandler = new DynamicEventHandler(proxy, handler);

        assertFalse(proxy.isHandled());
        dynamicEventHandler.onEvent(event);
        assertTrue(proxy.isHandled());
    }
}