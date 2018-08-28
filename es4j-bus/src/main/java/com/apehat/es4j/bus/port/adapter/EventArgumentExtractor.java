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

import com.apehat.es4j.bus.event.Event;
import com.apehat.es4j.util.ArgumentExtractor;
import com.apehat.es4j.util.ReflectionArgumentExtractor;
import com.apehat.es4j.util.Value;

/**
 * @author hanpengfei
 * @since 1.0
 */
final class EventArgumentExtractor implements ArgumentExtractor<Event> {

    private static final String PREFIX = Event.EVENT + '.';
    private static final ArgumentExtractor<Object> EXTRACTOR = new ReflectionArgumentExtractor();

    @Override
    public Value<?> extract(String name, Event event) {
        if (Event.OCCURRED_ON.equals(name)) {
            return new Value<>(event.occurredOn());
        } else if (Event.TYPE.equals(name)) {
            return new Value<>(event.type());
        } else if (Event.SOURCE.equals(name)) {
            return new Value<>(event.source());
        } else if (Event.EVENT.equals(name)) {
            return new Value<>(event.prototype());
        } else {
            return EXTRACTOR.extract(clearName(name), event.prototype());
        }
    }

    private String clearName(String name) {
        return name.startsWith(PREFIX) ? name.substring(PREFIX.length()) : name;
    }
}
