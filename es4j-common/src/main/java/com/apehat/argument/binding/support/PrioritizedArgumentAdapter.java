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

package com.apehat.argument.binding.support;

import com.apehat.Value;
import com.apehat.argument.binding.ArgumentAdapter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class PrioritizedArgumentAdapter implements ArgumentAdapter {

    private final List<ArgumentAdapter> adapters = new LinkedList<>();

    @Override
    public Value<?> adapt(String alias, Object prototype) {
        Objects.requireNonNull(alias, "Alias must not be null");
        return resolve(parentElse(alias, prototype), field(alias));
    }

    public void registerAdapter(ArgumentAdapter adapter) {
        this.adapters.add(adapter);
    }

    private Object parentElse(String alias, Object prototype) {
        final String parentAlias = parentAlias(alias);
        if (parentAlias == null) {
            return prototype;
        }
        final Value<?> parentValue = adapt(parentAlias, prototype);
        return (parentValue == null) ? null : parentValue.get();
    }

    private String parentAlias(String alias) {
        final int idx = alias.lastIndexOf(SEPARATOR);
        return (idx == -1) ? null : alias.substring(0, idx);
    }

    private String field(String name) {
        return name.substring(name.lastIndexOf(SEPARATOR) + 1);
    }

    private Value<?> resolve(Object obj, String name) {
        for (ArgumentAdapter adapter : adapters) {
            Value<?> value = adapter.adapt(name, obj);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
