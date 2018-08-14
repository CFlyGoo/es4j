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

import com.apehat.es4j.NestCheckException;
import com.apehat.es4j.util.ObjectUtils;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
@SuppressWarnings("WeakerAccess")
public final class Event {

    /* Attribute name  */

    public static final String OCCURRED_ON = "occurredOn";
    public static final String TYPE = "type";
    public static final String SOURCE = "source";
    public static final String EVENT = "event";

    /* Separators */

    private static final String SEPARATOR = ".";
    private static final String START = EVENT + SEPARATOR;

    /* Fields */

    private final long occurredOn;
    private final Object prototype;
    private final String source;
    private final Type type;

    public Event(long occurredOn, Object prototype, Type type, String source) {
        Objects.requireNonNull(prototype, "Event prototype must not be null.");
        this.prototype = ObjectUtils.deepClone(prototype);
        this.type = Objects.requireNonNull(type, "Event type must not be null.");
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
            Objects.equals(source, that.source) &&
            Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        int result = 253;
        result = 31 * result + Objects.hash(occurredOn);
        result = 31 * result + Objects.hash(prototype);
        result = 31 * result + Objects.hash(source);
        result = 31 * result + Objects.hash(type);
        return result;
    }

    public Object get(String name) {
        Objects.requireNonNull(name, "Attribute name must not be null.");
        if (OCCURRED_ON.equals(name)) {
            return occurredOn();
        } else if (TYPE.equals(name)) {
            return type();
        } else if (SOURCE.equals(name)) {
            return source();
        } else if (EVENT.equals(name)) {
            return prototype();
        }
        Object value = getPrototypeAttribute(prototype, name);
        return ObjectUtils.deepClone(value);
    }

    private Object getPrototypeAttribute(Object prototype, String name) {
        if (prototype == null) {
            return null;
        }
        if (name.startsWith(START)) {
            name = name.substring(START.length());
        }
        final int index = name.indexOf(SEPARATOR);
        String fieldName = (index == -1) ? name : name.substring(0, index);
        final Class<?> prototypeClass = prototype.getClass();
        Object value = null;
        try {
            Field field = prototypeClass.getDeclaredField(fieldName);
            boolean flag = ObjectUtils.toAccessible(field);
            try {
                value = field.get(prototype);
            } catch (IllegalAccessException e) {
                // will not happen
                throw new NestCheckException(e);
            } finally {
                field.setAccessible(flag);
            }
        } catch (NoSuchFieldException e) {
            throw new NestCheckException(e);
        }
        if (index != -1) {
            value = getPrototypeAttribute(value, name.substring(index));
        }
        return value;
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

    public Type type() {
        return type;
    }
}
