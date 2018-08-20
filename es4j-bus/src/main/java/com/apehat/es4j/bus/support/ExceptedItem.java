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

package com.apehat.es4j.bus.support;

import com.apehat.es4j.util.AbstractClassItem;
import com.apehat.es4j.util.Item;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
final class ExceptedItem extends AbstractClassItem {

    ExceptedItem(Class<?> value) {
        super(value);
    }

    private ExceptedItem(Class<?> value, Set<Item<Class<?>>> slots) {
        super(value, slots);
    }

    @Override
    public Item<Class<?>> newInstance(Class<?> value, Set<Item<Class<?>>> slots) {
        return new ExceptedItem(value, slots);
    }

    @Override
    public Item<Class<?>> newReverseInstance(Class<?> cls) {
        return new IncludeItem(cls);
    }

    @Override
    public boolean isEnable() {
        return false;
    }
}
