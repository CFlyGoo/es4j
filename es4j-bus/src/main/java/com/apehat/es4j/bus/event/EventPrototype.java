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
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class EventPrototype {

    private FieldValueFinder finder;
    private final Object root;

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
        if (finder == null) {
            finder = new FieldValueFinder(root);
        }
        return ObjectUtils.deepClone(finder.lookup(name));
    }
}
