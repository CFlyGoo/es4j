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

import com.apehat.es4j.util.FieldValueFinder;
import com.apehat.es4j.util.ObjectUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class EventPrototype {

    private static final String SEPARATOR = ".";
    private static final String START = Event.EVENT + SEPARATOR;

    private final Object prototype;
    private transient volatile Map<String, Object> cachedNameValue = new HashMap<>();

    public EventPrototype(Object prototype) {
        Objects.requireNonNull(prototype, "Event prototype must not be null.");
        this.prototype = ObjectUtils.deepClone(prototype);
    }

    public Object get(String name) {
        assert name != null;
        if (name.startsWith(START)) {
            name = name.substring(START.length());
        }
        Object value;
        if ((value = cachedNameValue.get(name)) == null) {
            FieldValueFinder finder = new FieldValueFinder();
            value = finder.getFiledValue(prototype, name);
            cachedNameValue.put(name, value);
        }
        return ObjectUtils.deepClone(value);
    }

    public Object getPrototype() {
        return ObjectUtils.deepClone(prototype);
    }
}
