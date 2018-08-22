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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import com.apehat.es4j.bus.support.MockDynamicEventHandler;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class EventBusTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBusTest.class);

    @Test
    public void testPublishBeforeRegisterEventHandler() {
        // Terminal will print warn log
        final EventBus bus = provisionEventBus();
        bus.publish(new EventPublished());

        final boolean[] handled = {false};
        bus.subscribe(EventPublished.class, event -> handled[0] = true);

        assertFalse(handled[0]);
    }

    @Test
    public void testPublishAfterRegisteredEventHandler() {
        final EventBus bus = provisionEventBus();

        final boolean[] handled = {false};
        bus.subscribe(EventPublished.class, event -> handled[0] = true);

        bus.publish(new EventPublished());
        assertTrue(handled[0]);
    }

    @Test
    public void testPublishBeforeRegisterMethod() {
        // Terminal will print warn log
        final EventBus bus = provisionEventBus();
        bus.publish(new EventPublished());

        final MockDynamicEventHandler handler = new MockDynamicEventHandler();
        bus.subscribe(EventPublished.class, handler, handler.getHandleMethod());

        assertFalse(handler.isHandled());
    }

    @Test
    public void testPublishAfterRegisteredMethod() {
        final EventBus bus = provisionEventBus();
        final MockDynamicEventHandler handler = new MockDynamicEventHandler();
        bus.subscribe(EventPublished.class, handler, handler.getHandleMethod());
        bus.publish(new EventPublished());
        assertTrue(handler.isHandled());
    }

    @Test
    public void testSubmitBeforeRegisterEventHandler() throws Exception {
        // Terminal will print warn log
        final EventBus bus = provisionEventBus();
        bus.submit(new EventSubmitted());

        final boolean[] handled = {false};
        bus.subscribe(EventSubmitted.class, event -> handled[0] = true);

        Thread.sleep(1000);
        assertFalse(handled[0]);
    }

    @Test
    public void testSubmitAfterRegisteredEventHandler() throws Exception {
        final EventBus bus = provisionEventBus();
        final boolean[] handled = {false};
        bus.subscribe(EventSubmitted.class, event -> handled[0] = true);
        bus.submit(new EventSubmitted());
        Thread.sleep(50);
        assertTrue(handled[0]);
    }

    @Test
    public void testSubmitBeforeRegisterMethod() throws Exception {
        final EventBus bus = provisionEventBus();
        bus.submit(new EventSubmitted());

        final MockDynamicEventHandler handler = new MockDynamicEventHandler();
        bus.subscribe(EventSubmitted.class, handler, handler.getHandleMethod());

        Thread.sleep(100);
        assertFalse(handler.isHandled());
    }

    @Test
    public void testSubmitAfterRegisteredMethod() throws Exception {
        final EventBus bus = provisionEventBus();
        final MockDynamicEventHandler handler = new MockDynamicEventHandler();
        bus.subscribe(EventSubmitted.class, handler, handler.getHandleMethod());
        bus.submit(new EventSubmitted());
        Thread.sleep(50);
        assertTrue(handler.isHandled());
    }

    @Test
    public void testMultipleRegisterWithSameHandlerThenPublish() {
        AtomicInteger handleCount = new AtomicInteger();
        final EventBus bus = provisionEventBus();
        registerSameHandler(handleCount, bus, "publish");
        bus.publish(new EventPublished());
        assertEquals(handleCount.get(), 1);
    }

    @Test
    public void testMultipleRegisterWithSameHandlerThenSubmit() throws Exception {
        final AtomicInteger handleCount = new AtomicInteger();
        final EventBus bus = provisionEventBus();
        registerSameHandler(handleCount, bus, "submit");
        bus.submit(new EventSubmitted());
        Thread.sleep(100);
        assertEquals(handleCount.get(), 1);
    }

    private void registerSameHandler(final AtomicInteger handleCount, EventBus bus, String action) {
        int registerCount;
        //noinspection StatementWithEmptyBody
        while ((registerCount = new Random().nextInt(50)) < 10) {
        }

        final EventHandler handler = event -> LOGGER.debug(
            "multiple register with same handler then {}, handle time {}",
            action, handleCount.incrementAndGet());
        for (int i = 0; i < registerCount; i++) {
            bus.subscribe(handler);
        }
    }

    private EventBus provisionEventBus() {
        return new EventBus();
    }

    // TODO  test submit by multiple thread
}
