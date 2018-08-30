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

package com.apehat.es4j.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ReflectionArgumentExtractor implements ArgumentExtractor<Object> {

    private static final char SEPARATOR = '.';

    private static final ConcurrentCache<Object, Map<String, Value<?>>> CACHE =
        new ConcurrentCache<>(100);

    @Override
    public Value<?> extract(String alias, Object prototype) {
        Objects.requireNonNull(alias, "Cannot extract with null");
        final Map<String, Value<?>> cache = CACHE.get(prototype);
        return (cache != null && cache.containsKey(alias)) ?
            cache.get(alias) :
            cache(prototype, alias, doExtract(prototype, alias));
    }

    private Value<?> cache(Object prototype, String name, Value<?> result) {
        Map<String, Value<?>> cache = CACHE.get(prototype);
        if (cache == null) {
            cache = new HashMap<>();
        }
        cache.put(name, result);
        CACHE.put(prototype, cache);
        return result;
    }

    private Value<?> doExtract(Object prototype, String name) {
        return resolve(parentElse(prototype, name), field(name));
    }

    private Object parentElse(Object prototype, String name) {
        Object parent = prototype;
        final String parentName = parentName(name);
        if (parentName != null) {
            final Value<?> parentValue = extract(parentName, prototype);
            parent = (parentValue == null) ? null : parentValue.get();
        }
        return parent;
    }

    private String parentName(String name) {
        final int idx = name.lastIndexOf(SEPARATOR);
        return (idx == -1) ? null : name.substring(0, idx);
    }

    private String field(String name) {
        return name.substring(name.lastIndexOf(SEPARATOR) + 1);
    }

    private Value<?> resolve(Object obj, String name) {
        assert name != null;
        if (obj == null) {
            return null;
        }
        try {
            return new Value<>(ReflectionUtils.access(
                obj.getClass().getDeclaredField(name),
                accessible -> accessible.get(obj)));
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
