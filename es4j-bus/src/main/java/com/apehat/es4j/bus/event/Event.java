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

package com.apehat.es4j.bus.event;

import com.apehat.es4j.common.util.ObjectUtils;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
@SuppressWarnings("WeakerAccess")
public final class Event {

    public static final String OCCURRED_ON = "occurredOn";
    public static final String TYPE = "type";
    public static final String SOURCE = "source";
    public static final String EVENT = "event";

    private final long occurredOn;
    private final Object prototype;
    private final String source;

    Event(long occurredOn, Object prototype, String source) {
        this.prototype = ObjectUtils.deepClone(
            Objects.requireNonNull(prototype, "Event prototype must not be null"));
        this.occurredOn = occurredOn;
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event that = (Event) o;
        return occurredOn == that.occurredOn &&
            Objects.equals(prototype, that.prototype) &&
            Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        int result = 253;
        result = 31 * result + Objects.hash(occurredOn);
        result = 31 * result + Objects.hash(prototype);
        result = 31 * result + Objects.hash(source);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
            "type=" + type() +
            ", occurredOn=" + occurredOn +
            ", prototype=" + prototype +
            ", source='" + source + '\'' +
            '}';
    }

    public long occurredOn() {
        return occurredOn;
    }

    public Object prototype() {
        return ObjectUtils.deepClone(prototype);
    }

    public String source() {
        return source;
    }

    public Class<?> type() {
        return prototype.getClass();
    }
}
