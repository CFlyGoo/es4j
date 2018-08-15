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

import com.apehat.es4j.util.FieldValueFinder;
import com.apehat.es4j.util.ObjectUtils;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
final class EventPrototype {

    private static final String SEPARATOR = ".";
    private static final String START = Event.EVENT + SEPARATOR;

    private final Object prototype;

    EventPrototype(Object prototype) {
        Objects.requireNonNull(prototype, "Event prototype must not be null.");
        this.prototype = ObjectUtils.deepClone(prototype);
    }

    Object get(String name) {
        assert name != null;
        if (name.startsWith(START)) {
            name = name.substring(START.length());
        }
        FieldValueFinder finder = new FieldValueFinder();
        Object value = finder.getFiledValue(prototype, name);
        return ObjectUtils.deepClone(value);
    }

    Object getPrototype() {
        return ObjectUtils.deepClone(prototype);
    }
}
