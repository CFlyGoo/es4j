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

package com.apehat.es4j.bus.subscriber.support;

import com.apehat.es4j.bus.Type;
import com.apehat.es4j.bus.subscriber.Subscriber;
import com.apehat.es4j.bus.subscriber.SubscriberRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class CopyOnArraySubscriberRepository implements SubscriberRepository {

    private final Set<Subscriber> subscribers = new CopyOnWriteArraySet<>();

    @Override
    public void save(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public Set<Subscriber> subscriberWithType(Type type) {
        final Set<Subscriber> registeredSubscribers = this.subscribers;
        final Set<Subscriber> subscribers = new HashSet<>();
        for (Subscriber subscriber : registeredSubscribers) {
            if (subscriber.subscriptionType().equals(type)) {
                subscribers.add(subscriber);
            }
        }
        return subscribers;
    }
}