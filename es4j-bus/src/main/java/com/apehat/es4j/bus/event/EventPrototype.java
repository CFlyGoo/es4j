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

    private static final char SEPARATOR = '.';

    private final Object root;

    private transient volatile Map<String, Result<?>> cache = new HashMap<>();

    public EventPrototype(Object root) {
        Objects.requireNonNull(root, "Event prototype root must not be null.");
        this.root = ObjectUtils.deepClone(root);
    }

    Object root() {
        return ObjectUtils.deepClone(root);
    }

    Class<?> type() {
        return root.getClass();
    }

    Object get(String name) {
        assert name != null;
        return ObjectUtils.deepClone(lookup(name).value());
    }

    private Result<?> lookup(String name) {
        Result<?> result = cache.get(name);
        if (result == null) {
            result = resolve(parent(name).value(), fieldName(name));
            cache.put(name, result);
        }
        return result;
    }

    private Result<?> parent(String name) {
        final String parent = parentNameOf(name);
        return parent.isEmpty() ? new Result<>(root) : lookup(parent);
    }

    private String parentNameOf(String name) {
        final int idx = name.lastIndexOf(SEPARATOR);
        return (idx == -1) ? "" : name.substring(0, idx);
    }

    private String fieldName(String name) {
        return name.substring(name.lastIndexOf(SEPARATOR) + 1);
    }

    private Result<?> resolve(Object source, String name) {
        return new Result<>(new FieldValueFinder().getFiledValue(source, name));
    }
}
