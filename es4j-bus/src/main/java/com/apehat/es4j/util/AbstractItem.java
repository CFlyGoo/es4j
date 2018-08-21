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

import com.apehat.es4j.util.graph.AcmeDirectedGraph;
import com.apehat.es4j.util.graph.DirectedGraph;
import com.apehat.es4j.util.graph.Indicator;
import com.apehat.es4j.util.layer.DirectedGraphQuantizer;
import com.apehat.es4j.util.layer.Layer;
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

    protected AbstractItem(T value) {
        this(value, Collections.emptySet());
    }

    protected AbstractItem(T value, Set<Item<T>> slots) {
        this.value = Objects.requireNonNull(value, "value must not be null");
        this.slots = Collections.unmodifiableSet(rebuildSlots(slots));
    }

    @Override
    public Item<T> add(T cls) {
        if (!isManageable(cls)) {
            return this;
        }
        if (cls == value) {
            return slots.isEmpty() ? this : newInstance(value, Collections.emptySet());
        }

        final Set<Item<T>> newSlots = new HashSet<>();
        for (Item<T> slot : slots) {
            if (slot.value() != cls) {
                newSlots.add(slot.contains(cls) ? slot.remove(cls) : slot);
            }
        }
        return newInstance(value, newSlots);
    }

    @Override
    public Item<T> remove(T cls) {
        if (!isManageable(cls)) {
            return this;
        }
        if (cls == value) {
            throw new IllegalStateException("Cannot remove myself with " + cls);
        }
        boolean removed = false;
        final Set<Item<T>> newSlots = new HashSet<>();
        for (Item<T> slot : slots) {
            if (slot.value() == cls) {
                newSlots.add(newReverseInstance(slot.value()));
                removed = true;
            } else if (slot.isManageable(cls)) {
                newSlots.add(slot.add(cls));
                removed = true;
            } else {
                newSlots.add(slot);
            }
        }
        if (!removed) {
            newSlots.add(newReverseInstance(cls));
        }
        return newInstance(value, newSlots);
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

    public Set<Item<T>> slots() {
        return Collections.unmodifiableSet(slots);
    }

    protected Set<Item<T>> rebuildSlots(Set<Item<T>> slots) {
        if (slots == null || slots.isEmpty()) {
            return slots;
        }
        final DirectedGraph<Item<T>> graph = createNewDirectedGraph(slots);
        final Set<Item<T>> mergeable = graph.items();
        final Set<Item<T>> top = layer(graph).top().currentLayerItems();
        if (top.size() == mergeable.size()) {
            return slots;
        }
        final Set<Item<T>> newSlots = new HashSet<>(top);
        mergeable.removeAll(top);
        for (Item<T> item : mergeable) {
            Set<Item<T>> reachableSet = graph.getReachableSet(item);
            for (Item<T> reachable : reachableSet) {
                if (newSlots.contains(reachable)) {
                    final Set<Item<T>> temp = new HashSet<>(reachable.slots());
                    temp.addAll(item.slots());
                    newSlots.remove(reachable);
                    newSlots.add(reachable.newInstance(reachable.value(), temp));
                }
            }
        }
        return newSlots;
    }

    protected abstract Indicator<Item<T>> getIndicator();

    protected Layer<Item<T>> layer(DirectedGraph<Item<T>> graph) {
        return new DirectedGraphQuantizer<>(graph).calculateLayer();
    }

    protected DirectedGraph<Item<T>> createNewDirectedGraph(Set<Item<T>> slots) {
        return new AcmeDirectedGraph<>(slots, getIndicator());
    }
}
