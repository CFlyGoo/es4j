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
import com.apehat.es4j.bus.event.PendingEvent;
import com.apehat.es4j.bus.subscriber.Subscriber;
import com.apehat.es4j.bus.subscriber.SubscriberIdentityService;
import com.apehat.es4j.bus.subscriber.SubscriberRepository;
import com.apehat.es4j.bus.subscriber.support.CopyOnWriteSubscriberRepository;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.TimeUnit;

/**
 * @author hanpengfei
 * @since 1.0
 */
@SuppressWarnings("WeakerAccess")
public final class EventBus {

    private SubscriberRepository subscriberRepo = new CopyOnWriteSubscriberRepository();
    private final SubscriberIdentityService subscriberIdentityService =
        new SubscriberIdentityService(subscriberRepo);
    private EventIdentityService eventIdentityService = new EventIdentityService();
    private Dispatcher dispatcher = new Dispatcher();

    private ExecutorService pool = new ThreadPoolExecutor(4, 4,
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>(), r -> new Thread(r, "es4j-bus-pool-" + r.hashCode()),
        new DiscardOldestPolicy());

    private AsyncDispatcher asyncDispatcher = new AsyncDispatcher(pool);

    /* Global subscribe */

    public void subscribe(EventHandler handler) {
        subscribe(Object.class, handler);
    }

    /* Type specified subscribe */

    public void subscribe(Class<?> type, EventHandler handler) {
        subscriberIdentityService.provisionSubscriber(type, handler);
    }

    /* Publish */

    public void publish(String source, Object event) {
        final PendingEvent pendingEvent = this.provisionEvent(source, event);
        dispatcher.dispatch(pendingEvent, this.subscribersOf(pendingEvent));
    }

    /* Submit */

    public void submit(String source, Object event) {
        final PendingEvent pendingEvent = this.provisionEvent(source, event);
        asyncDispatcher.dispatch(pendingEvent, this.subscribersOf(pendingEvent));
    }

    private Set<Subscriber> subscribersOf(PendingEvent event) {
        return subscriberIdentityService.subscribersWith(event.type());
    }

    private PendingEvent provisionEvent(String source, Object event) {
        return eventIdentityService.provisionEvent(event, source);
    }
}
