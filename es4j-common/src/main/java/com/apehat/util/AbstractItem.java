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

package com.apehat.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
abstract class AbstractItem<T> implements Item<T> {

    private final T value;
    private final Set<Item<T>> slots;

    protected AbstractItem() {
        this(null, null);
    }

    protected AbstractItem(T value, Set<Item<T>> slots) {
        this.value = value;
        this.slots = (slots == null) ? Collections.emptySet() : Collections.unmodifiableSet(slots);
    }

    @Override
    public Item<T> add(T value) {
        if (!isManageable(value)) {
            return this;
        }
        if (value == this.value) {
            return slots.isEmpty() ? this : newInstance(this.value, Collections.emptySet());
        }

        final Set<Item<T>> newSlots = new HashSet<>();
        for (Item<T> slot : slots) {
            if (slot.value() != value) {
                newSlots.add(slot.contains(value) ? slot.remove(value) : slot);
            }
        }
        return newInstance(this.value, newSlots);
    }

    @Override
    public Item<T> remove(T value) {
        if (!isManageable(value)) {
            return this;
        }
        if (value == this.value) {
            throw new IllegalStateException("Cannot remove myself with " + value);
        }
        boolean removed = false;
        final Set<Item<T>> newSlots = new HashSet<>();
        for (Item<T> slot : slots) {
            if (slot.value() == value) {
                newSlots.add(newReverseInstance(slot.value()));
                removed = true;
            } else if (slot.isManageable(value)) {
                newSlots.add(slot.add(value));
                removed = true;
            } else {
                newSlots.add(slot);
            }
        }
        if (!removed) {
            newSlots.add(newReverseInstance(value));
        }
        return newInstance(this.value, newSlots);
    }

    @Override
    public T value() {
        return value;
    }

    @Override
    public boolean contains(T cls) {
        if (!isManageable(cls)) {
            return false;
        }
        if (cls == value()) {
            return true;
        }
        for (Item<T> slot : slots()) {
            if (slot.contains(cls)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Set<Item<T>> slots() {
        return Collections.unmodifiableSet(slots);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractItem<?> that = (AbstractItem<?>) o;
        return Objects.equals(value, that.value) &&
            Objects.equals(slots, that.slots);
    }

    @Override
    public int hashCode() {
        int hash = 195;
        hash += 31 * hash + value.hashCode();
        hash += 31 * hash + slots.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "value=" + value +
            ", slots=" + slots +
            '}';
    }
}
