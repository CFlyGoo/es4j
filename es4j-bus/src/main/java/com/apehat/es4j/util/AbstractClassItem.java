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
import java.util.HashSet;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public abstract class AbstractClassItem extends AbstractItem<Class<?>> {

    private static final Indicator<Item<Class<?>>> INDICATOR =
        (o1, o2) -> o1.isEnable() == o2.isEnable() && o2.value().isAssignableFrom(o1.value());

    protected AbstractClassItem(Class<?> value) {
        super(value);
    }

    protected AbstractClassItem(Class<?> value, Set<Item<Class<?>>> slots) {
        super(value, slots);
    }

    @Override
    public final boolean isManageable(Class<?> cls) {
        return value().isAssignableFrom(cls);
    }

    public Set<Item<Class<?>>> rebuildSlots(Set<Item<Class<?>>> slots) {
        if (slots == null || slots.isEmpty()) {
            return slots;
        }
        DirectedGraph<Item<Class<?>>> graph = createNewDirectedGraph(slots);
        final Set<Item<Class<?>>> mergeable = graph.items();
        final Set<Item<Class<?>>> top = graph.getTop();
        if (top.size() == mergeable.size()) {
            return graph.items();
        }
        final Set<Item<Class<?>>> newSlots = new HashSet<>(top);
        mergeable.removeAll(top);
        for (Item<Class<?>> item : mergeable) {
            Set<Item<Class<?>>> reachableSet = graph.getReachableSet(item);
            for (Item<Class<?>> reachable : reachableSet) {
                if (newSlots.contains(reachable)) {
                    final Set<Item<Class<?>>> temp = new HashSet<>(reachable.slots());
                    temp.addAll(item.slots());
                    newSlots.remove(reachable);
                    newSlots.add(reachable.newInstance(reachable.value(), rebuildSlots(temp)));
                }
            }
        }
        return newSlots;
    }

    protected Indicator<Item<Class<?>>> getIndicator() {
        return INDICATOR;
    }

    protected DirectedGraph<Item<Class<?>>> createNewDirectedGraph(Set<Item<Class<?>>> slots) {
        return new AcmeDirectedGraph<>(slots, getIndicator());
    }
}
