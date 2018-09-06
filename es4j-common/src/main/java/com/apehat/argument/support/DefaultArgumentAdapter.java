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

package com.apehat.argument.support;

import com.apehat.Value;
import com.apehat.ConcurrentCache;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class DefaultArgumentAdapter extends PrioritizedArgumentAdapter {

    private final ConcurrentCache<Object, Map<String, Value<?>>> concurrentCache = new ConcurrentCache<>(
        16);

    public DefaultArgumentAdapter() {
        registerAdapter(new MethodNameAdapter());
        registerAdapter(new GetterNameAdapter());
        registerAdapter(new FieldNameAdapter());
    }

    @Override
    public Value<?> adapt(String alias, Object prototype) {
        Objects.requireNonNull(alias, "Alias must not be null");
        Map<String, Value<?>> cache = concurrentCache.get(prototype);
        return (cache != null && cache.containsKey(alias)) ?
            cache.get(alias) :
            cache(prototype, alias, super.adapt(alias, prototype));
    }

    private Value<?> cache(Object prototype, String name, Value<?> result) {
        Map<String, Value<?>> cache = concurrentCache.get(prototype);
        if (cache == null) {
            cache = new HashMap<>();
        }
        cache.put(name, result);
        concurrentCache.put(prototype, cache);
        return result;
    }
}
