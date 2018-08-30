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
import com.apehat.es4j.common.util.ArgumentExtractor;
import com.apehat.es4j.common.util.ReflectionArgumentExtractor;
import com.apehat.es4j.common.util.Value;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
final class EventArgumentExtractor implements ArgumentExtractor<Event> {

    private static final String PREFIX = Event.EVENT + '.';

    private static final Set<ArgumentExtractor<Event>> EXTRACTORS;

    private ArgumentExtractor<Object> prototypeExtractor = new ReflectionArgumentExtractor();

    static {
        Set<ArgumentExtractor<Event>> extractors = new LinkedHashSet<>();
        extractors.add(new OccurredOnExtractor());
        extractors.add(new TypeExtractor());
        extractors.add(new SourceExtractor());
        extractors.add(new PrototypeExtractor());
        EXTRACTORS = Collections.unmodifiableSet(extractors);
    }

    @Override
    public Value<?> extract(String alias, Event event) {
        for (ArgumentExtractor<Event> extractor : EXTRACTORS) {
            Value<?> value = extractor.extract(alias, event);
            if (value != null) {
                return value;
            }
        }
        return prototypeExtractor.extract(clearName(alias), event.prototype());
    }

    private static class OccurredOnExtractor implements ArgumentExtractor<Event> {

        @Override
        public Value<?> extract(String alias, Event prototype) {
            return Event.OCCURRED_ON.equals(alias) ? new Value<>(prototype.occurredOn()) : null;
        }
    }

    private static class TypeExtractor implements ArgumentExtractor<Event> {

        @Override
        public Value<?> extract(String alias, Event prototype) {
            return Event.TYPE.equals(alias) ? new Value<>(prototype.type()) : null;
        }
    }

    private static class SourceExtractor implements ArgumentExtractor<Event> {

        @Override
        public Value<?> extract(String alias, Event prototype) {
            return Event.SOURCE.equals(alias) ? new Value<>(prototype.source()) : null;
        }
    }

    private static class PrototypeExtractor implements ArgumentExtractor<Event> {

        @Override
        public Value<?> extract(String alias, Event prototype) {
            return Event.EVENT.equals(alias) ? new Value<>(prototype.prototype()) : null;
        }
    }

    private String clearName(String name) {
        return name.startsWith(PREFIX) ? name.substring(PREFIX.length()) : name;
    }
}
