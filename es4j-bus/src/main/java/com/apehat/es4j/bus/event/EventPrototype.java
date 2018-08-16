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

import com.apehat.es4j.Result;
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

    private final Object prototype;

    private transient volatile Map<String, Result> cachedNameResult = new HashMap<>();

    public EventPrototype(Object prototype) {
        Objects.requireNonNull(prototype, "Event prototype must not be null.");
        this.prototype = ObjectUtils.deepClone(prototype);
    }

    public Object getPrototype() {
        return ObjectUtils.deepClone(prototype);
    }

    Object get(String name) {
        assert name != null;
        final Result lookupValue = lookupCache(name);
        final Result result = (lookupValue == null) ? findByFinder(prototype, name) : lookupValue;
        cachedNameResult.putIfAbsent(name, result);
        return ObjectUtils.deepClone(result.value());
    }

    private Result lookupCache(String name) {
        Result result = cachedNameResult.get(name);
        if (result != null) {
            return result;
        }
        final int idx = name.lastIndexOf('.');
        if (idx == -1) {
            return null;
        }
        result = lookupCache(name.substring(0, idx));
        if (result != null) {
            result = findByFinder(result.value(), name.substring(idx + 1));
        }
        return result;
    }

    private Result findByFinder(Object source, String name) {
        FieldValueFinder finder = new FieldValueFinder();
        Object value = finder.getFiledValue(source, name);
        return new Result(value);
    }
}
