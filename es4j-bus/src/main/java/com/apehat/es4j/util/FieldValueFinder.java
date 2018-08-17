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

package com.apehat.es4j.util;

import com.apehat.es4j.NestedCheckException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author hanpengfei
 */
public class FieldValueFinder {

    private static final char SEPARATOR = '.';

    private final Object source;

    private transient volatile Map<String, Object> cache = new HashMap<>();

    public FieldValueFinder(Object source) {
        this.source = Objects.requireNonNull(source, "The source must not be null");
    }

    public Object lookup(String name) {
        Objects.requireNonNull(name, "Name must not be null");
        if (cache.containsKey(name)) {
            return cache.get(name);
        }
        final Object result = resolve(parent(name), field(name));
        cache.put(name, result);
        return result;
    }

    private Object parent(String name) {
        final String parentName = parentName(name);
        return parentName == null ? source : lookup(parentName);
    }

    private String parentName(String name) {
        final int idx = name.lastIndexOf(SEPARATOR);
        return (idx == -1) ? null : name.substring(0, idx);
    }

    private String field(String name) {
        return name.substring(name.lastIndexOf(SEPARATOR) + 1);
    }

    private Object resolve(Object obj, String name) {
        assert name != null;
        if (obj == null) {
            return null;
        }
        try {
            return ReflectionUtils.access(obj.getClass().getDeclaredField(name),
                accessible -> accessible.get(obj));
        } catch (NoSuchFieldException e) {
            throw new NestedCheckException(e);
        }
    }
}