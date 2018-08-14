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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.apehat.es4j.NotImplementedException;
import com.apehat.es4j.bus.support.MockDynamicEventHandler;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * The class {@code EventBusTest} bus used to test {@code EventBus} api.
 *
 * @author hanpengfei
 * @since 1.0
 */
public class EventBusTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBusTest.class);

    private EventBus eventBus;

    private boolean handled;

    public EventBusTest() {
        eventBus = new EventBus();
    }

    /**
     * Before publish, register a global subscriber to wait handle event
     */
    @BeforeMethod(groups = "publish", enabled = false)
    public void beforePublish() {
        String subscriberId = eventBus.subscribe(Object.class, event -> {
            handled = true;
            LOGGER.info("Handle event {}", event);
        });
        assertNotNull(subscriberId);
    }

    /**
     * Register a new EventHandler with arbitrary method
     */
    @Test(groups = "subscribe", expectedExceptions = NotImplementedException.class)
    public void testRegisterEventListenerWithMethod() {
        MockDynamicEventHandler subscriber = new MockDynamicEventHandler();
        String subscriberId = eventBus.register(subscriber, MockDynamicEventHandler.getEventHandler());
        assertNotNull(subscriberId);
        Set<String> subscriberIds = eventBus.allGlobalSubscribers();
        assertTrue(subscriberIds.contains(subscriberId));
    }

    /**
     * Register a new EventHandler which implemented {@code EventHandler} interface
     */
    @Test(groups = "subscribe", expectedExceptions = NotImplementedException.class)
    public void testRegisterEventListenerWithInterfaceImplementor() {
        String subscriberId = eventBus
            .register(event -> LOGGER.info("START handler with {}", event));
        assertNotNull(subscriberId);
        Set<String> subscriberIds = eventBus.allGlobalSubscribers();
        assertTrue(subscriberIds.contains(subscriberId));
    }

    /**
     * Register a new EventHandler and specified event type
     */
    @Test(groups = "subscribe", expectedExceptions = NotImplementedException.class)
    public void testRegisterEventListenerWithSpecifiedClass() {
        MockDynamicEventHandler subscriber = new MockDynamicEventHandler();
        String subscriberId =
            eventBus.subscribe(Object.class, subscriber, MockDynamicEventHandler.getEventHandler());
        assertNotNull(subscriberId);
        Set<String> subscriberIds = eventBus.allGlobalSubscribers();
        assertTrue(subscriberIds.contains(subscriberId));
    }

    /**
     * Register a new EventHandler and specified event type
     */
    @Test(groups = "subscribe", expectedExceptions = NotImplementedException.class)
    public void testRegisterEventListenerWithSpecifiedType() {
        MockDynamicEventHandler subscriber = new MockDynamicEventHandler();
        String subscriberId =
            eventBus.subscribe(Type.of(Object.class), subscriber, MockDynamicEventHandler.getEventHandler());
        assertNotNull(subscriberId);
        Set<String> subscriberIds = eventBus.allGlobalSubscribers();
        assertTrue(subscriberIds.contains(subscriberId));
    }

    /**
     * Register same handler with multiple time
     */
    @Test(groups = "subscribe", expectedExceptions = NotImplementedException.class)
    public void testMultipleRegisterWithSameHandler() {
        EventHandler handler = event -> LOGGER.info("Start handle {}", event);
        String first = eventBus.register(handler);
        String second = eventBus.register(handler);
        assertEquals(first, second);
    }

    /**
     * Synchronize publish a event
     */
    @Test(groups = "publish", expectedExceptions = NotImplementedException.class)
    public void testPublishEvent() {
        eventBus.publish(new Object(), new Callback() {
            @Override
            public void onSuccessfully() {
                handled = true;
            }
        });
        assertTrue(handled);
    }

    /**
     * Asynchronous publish a event
     */
    @Test(groups = "publish", expectedExceptions = NotImplementedException.class)
    public void testSubmitEvent() {
        eventBus.submit(new Object(), new Callback() {
            @Override
            public void onSuccessfully() {
                handled = true;
            }
        });
        assertTrue(handled);
    }
}
