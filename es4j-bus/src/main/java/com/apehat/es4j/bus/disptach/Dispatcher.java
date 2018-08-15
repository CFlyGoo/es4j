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

package com.apehat.es4j.bus.disptach;

import com.apehat.es4j.bus.Type;
import com.apehat.es4j.bus.event.PendingEvent;
import com.apehat.es4j.bus.subscriber.Subscriber;
import com.apehat.es4j.bus.subscriber.SubscriberRepository;
import java.util.Objects;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class Dispatcher {

    private SubscriberRepository subscriberRepo;

    public Dispatcher(SubscriberRepository subscriberRepo) {
        this.subscriberRepo = Objects.requireNonNull(
            subscriberRepo, "Subscriber repository must not be null");
    }

    public void dispatch(PendingEvent pendingEvent) {
        Type type = pendingEvent.type();
        Set<Subscriber> subscribers = subscriberRepo.subscriberWithType(type);
        for (Subscriber subscriber : subscribers) {
            subscriber.onEvent(pendingEvent);
        }
    }
}
