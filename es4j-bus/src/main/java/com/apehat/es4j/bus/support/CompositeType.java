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

import com.apehat.es4j.bus.Type;
import com.apehat.es4j.util.AbstractClassItem;
import com.apehat.es4j.util.Item;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class CompositeType implements Type {

    private static final long serialVersionUID = -467998764902780604L;
    private static final ClassItemsRebuildHelper ITEMS_REBUILD_HELPER = new ClassItemsRebuildHelper();

    private static Set<Item<Class<?>>> items(Class<?>... types) {
        if (types == null || types.length == 0) {
            throw new IllegalArgumentException("Must specified types");
        }
        final Set<Class<?>> set = new HashSet<>(Arrays.asList(types));
        final Set<Item<Class<?>>> items = new HashSet<>();
        for (Class<?> cls : set) {
            items.add(new IncludeItem(cls));
        }
        return items;
    }

    private Set<Item<Class<?>>> items;

    public CompositeType(Class<?>... types) {
        this(items(types));
    }

    private CompositeType(Set<Item<Class<?>>> items) {
        this.items = ITEMS_REBUILD_HELPER.rebuildSlots(items);
    }

    @Override
    public Type add(Class<?> type) {
        if (isAssignableFrom(type)) {
            return this;
        }
        final Set<Item<Class<?>>> newItems = new HashSet<>();
        boolean added = false;
        for (Item<Class<?>> item : items) {
            if (item.value().isAssignableFrom(type)) {
                newItems.add(item.add(type));
                added = true;
            }
        }
        if (!added) {
            newItems.add(new IncludeItem(type));
        }
        return new CompositeType(newItems);
    }

    @Override
    public Type remove(Class<?> type) {
        if (!isAssignableFrom(type)) {
            return this;
        }
        final Set<Item<Class<?>>> newItems = new HashSet<>();
        for (Item<Class<?>> item : items) {
            if (item.value() == type) {
                continue;
            }
            if (item.value().isAssignableFrom(type)) {
                newItems.add(item.remove(type));
            } else {
                newItems.add(item);
            }
        }
        return new CompositeType(newItems);
    }

    @Override
    public boolean isAssignableFrom(Class<?> cls) {
        for (Item<Class<?>> item : items) {
            if (item.contains(cls)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompositeType that = (CompositeType) o;
        return Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        int hash = 157;
        hash += 31 * hash + items.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "CompositeType{" +
            "items=" + items +
            '}';
    }

    private static class ClassItemsRebuildHelper extends AbstractClassItem {

        private ClassItemsRebuildHelper() {
            super(ClassItemsRebuildHelper.class);
        }

        @Override
        public Item<Class<?>> newInstance(Class<?> value, Set<Item<Class<?>>> slots) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Item<Class<?>> newReverseInstance(Class<?> cls) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEnable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Item<Class<?>>> rebuildSlots(Set<Item<Class<?>>> slots) {
            return super.rebuildSlots(slots);
        }
    }
}
