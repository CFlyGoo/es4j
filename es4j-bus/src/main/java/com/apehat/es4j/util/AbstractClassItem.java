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

import com.apehat.es4j.util.graph.Indicator;
import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public abstract class AbstractClassItem extends AbstractItem<Class<?>> implements Serializable {

    private static final long serialVersionUID = 2523147870883695061L;
    private static final Indicator<Item<Class<?>>> INDICATOR =
        (o1, o2) -> o1.isEnable() == o2.isEnable() && o2.value().isAssignableFrom(o1.value());

    protected AbstractClassItem(Class<?> value) {
        this(value, Collections.emptySet());
    }

    protected AbstractClassItem(Class<?> value, Set<Item<Class<?>>> slots) {
        super(Objects.requireNonNull(value, "Class must not be null"), slots);
    }

    @Override
    public final boolean isManageable(Class<?> value) {
        return value().isAssignableFrom(value);
    }

    @Override
    protected Indicator<Item<Class<?>>> getIndicator() {
        return INDICATOR;
    }
}
