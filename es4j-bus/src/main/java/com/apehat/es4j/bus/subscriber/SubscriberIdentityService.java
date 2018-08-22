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

package com.apehat.es4j.bus.subscriber;

import com.apehat.es4j.bus.EventHandler;
import com.apehat.es4j.bus.Type;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class SubscriberIdentityService {

    private final SubscriberRepository subscriberRepo;

    public SubscriberIdentityService(SubscriberRepository subscriberRepo) {
        this.subscriberRepo = Objects.requireNonNull(subscriberRepo,
            "Subscriber repository must not be null");
    }

    public String provisionSubscriber(Class<?> type, Object handler, Method handleMethod) {
        Subscriber subscriber =
            new Subscriber(new NormalHandlerDescriptor(handler, handleMethod), Type.of(type));
        this.subscriberRepo.save(subscriber);
        return subscriber.id();
    }

    public String provisionSubscriber(Class<?> type, EventHandler handler) {
        Subscriber subscriber = new Subscriber(new PlainHandleDescriptor(handler), Type.of(type));
        this.subscriberRepo.save(subscriber);
        return subscriber.id();
    }

    public String provisionSubscriber(Class<?> type, Method handler) {
        final Subscriber subscriber = new Subscriber(new StaticHandlerDescriptor(handler),
            Type.of(type));
        this.subscriberRepo.save(subscriber);
        return subscriber.id();
    }
}
