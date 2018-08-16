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

import com.apehat.es4j.bus.disptach.AsyncDispatcher;
import com.apehat.es4j.bus.disptach.Dispatcher;
import com.apehat.es4j.bus.event.PendingEvent;
import com.apehat.es4j.bus.subscriber.CopyOnArraySubscriberRepository;
import com.apehat.es4j.bus.subscriber.Subscriber;
import com.apehat.es4j.bus.subscriber.SubscriberRepository;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
@SuppressWarnings("WeakerAccess")
public final class EventBus {

    private SubscriberRepository subscriberRepo = new CopyOnArraySubscriberRepository();

    private Dispatcher dispatcher = new Dispatcher(subscriberRepo);
    private AsyncDispatcher asyncDispatcher = new AsyncDispatcher(subscriberRepo);


    /* Global subscribe */

    public String register(EventHandler handler) {
        return subscribe(Object.class, handler);
    }

    public String register(Object subscriber, Method handler) {
        return subscribe(Object.class, subscriber, handler);
    }

    /* Type specified subscribe */

    public String subscribe(Class<?> type, EventHandler handler) {
        return subscribe(Type.of(type), handler);
    }

    public String subscribe(Class<?> type, Object subscriber, Method handler) {
        return subscribe(Type.of(type), subscriber, handler);
    }

    /* Publish */

    public String subscribe(Type type, EventHandler handler) {
        Subscriber subscriber = new Subscriber(handler, type);
        this.subscriberRepo.save(subscriber);
        return subscriber.id();
    }

    public String subscribe(Type type, Object subscriber, Method handler) {
        DynamicEventHandler dynamicEventHandler = new DynamicEventHandler(subscriber, handler);
        return subscribe(type, dynamicEventHandler);
    }

    public void publish(Object event) {
        publish(Type.of(event.getClass()), event);
    }

    public void publish(Type type, Object event) {
        PendingEvent pendingEvent = new PendingEvent(event, null, type);
        dispatcher.dispatch(pendingEvent);
    }

    public void publish(Object event, Callback callback) {
        publish(Type.of(event.getClass()), event, callback);
    }

    public void publish(Type type, Object event, Callback callback) {
        try {
            PendingEvent pendingEvent = new PendingEvent(event, null, type);
            dispatcher.dispatch(pendingEvent);
            callback.onSuccessfully();
        } catch (RuntimeException e) {
            callback.onFailure();
        }
    }
    /* Submit */

    public void submit(Object event) {
        submit(Type.of(event.getClass()), event);
    }

    public void submit(Type type, Object event) {
        PendingEvent pendingEvent = new PendingEvent(event, null, type);
        asyncDispatcher.dispatch(pendingEvent);
    }

    public void submit(Object event, Callback callback) {
        submit(Type.of(event.getClass()), event, callback);
    }

    public void submit(Type type, Object event, Callback callback) {
        PendingEvent pendingEvent = new PendingEvent(event, null, type);
        asyncDispatcher.dispatch(pendingEvent, callback);
    }

    /* Query */

    public Set<String> allGlobalSubscribers() {
        return subscribersOf(Object.class);
    }

    public Set<String> subscribersOf(Class<?> type) {
        return subscribersOf(Type.of(type));
    }

    public Set<String> subscribersOf(Type type) {
        Set<Subscriber> subscribers = subscriberRepo.subscriberWithType(type);
        Set<String> subscriberIds = new HashSet<>();
        for (Subscriber subscriber : subscribers) {
            subscriberIds.add(subscriber.id());
        }
        return subscriberIds;
    }
}
