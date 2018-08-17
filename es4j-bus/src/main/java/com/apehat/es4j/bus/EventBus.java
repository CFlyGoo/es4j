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
import com.apehat.es4j.bus.event.EventIdentityService;
import com.apehat.es4j.bus.subscriber.Subscriber;
import com.apehat.es4j.bus.subscriber.SubscriberIdentityService;
import com.apehat.es4j.bus.subscriber.SubscriberRepository;
import com.apehat.es4j.bus.subscriber.support.CopyOnArraySubscriberRepository;
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
    private EventIdentityService eventIdentityService = new EventIdentityService();
    private final SubscriberIdentityService subscriberIdentityService =
        new SubscriberIdentityService(subscriberRepo);

    private Dispatcher dispatcher = new Dispatcher(subscriberRepo);
    private AsyncDispatcher asyncDispatcher = new AsyncDispatcher(subscriberRepo);

    /* Global subscribe */

    public String subscribe(EventHandler handler) {
        return subscribe(Object.class, handler);
    }

    public String subscribe(Method handler) {
        return subscribe(Object.class, handler);
    }

    public String subscribe(Object handler, Method handleMethod) {
        return subscribe(Object.class, handler, handleMethod);
    }

    /* Type specified subscribe */

    public String subscribe(Class<?> type, EventHandler handler) {
        return subscribe(Type.of(type), handler);
    }

    public String subscribe(Class<?> type, Method handler) {
        return subscribe(Type.of(type), handler);
    }

    public String subscribe(Class<?> type, Object handler, Method handleMethod) {
        return subscribe(Type.of(type), handler, handleMethod);
    }

    public String subscribe(Type type, EventHandler handler) {
        return subscriberIdentityService.provisionSubscriber(type, handler);
    }

    public String subscribe(Type type, Method handler) {
        return subscriberIdentityService.provisionSubscriber(type, handler);
    }

    public String subscribe(Type type, Object handler, Method handleMethod) {
        return subscriberIdentityService.provisionSubscriber(type, handler, handleMethod);
    }

    /* Publish */

    public void publish(Object event) {
        dispatcher.dispatch(eventIdentityService.provisionEvent(event, null));
    }

    public void publish(Object event, Callback callback) {
        try {
            dispatcher.dispatch(eventIdentityService.provisionEvent(event, null));
            callback.onSuccessfully();
        } catch (RuntimeException e) {
            callback.onFailure();
        }
    }

    /* Submit */

    public void submit(Object event) {
        asyncDispatcher.dispatch(eventIdentityService.provisionEvent(event, null));
    }

    public void submit(Object event, Callback callback) {
        asyncDispatcher.dispatch(eventIdentityService.provisionEvent(event, null), callback);
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
