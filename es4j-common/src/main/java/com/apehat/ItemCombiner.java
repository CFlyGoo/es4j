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

package com.apehat;

import com.apehat.graph.Digraph;
import com.apehat.layer.DigraphLayerAnalyzer;
import com.apehat.layer.Layer;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ItemCombiner<T> {

    private final Digraph<Item<T>> graph;

    public ItemCombiner(Digraph<Item<T>> graph) {
        this.graph = Objects.requireNonNull(graph, "Digraph must not be null");
    }

    public Set<Item<T>> combine() {
        final Set<Item<T>> mergeable = graph.vertices();
        final Set<Item<T>> top = layer(graph).top().currentLayerItems();
        if (top.size() == mergeable.size()) {
            return graph.vertices();
        }
        final Set<Item<T>> newSlots = new HashSet<>(top);
        mergeable.removeAll(top);
        for (Item<T> item : mergeable) {
            Set<Item<T>> reachableSet = graph.getReachableVertices(item);
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

    protected Layer<Item<T>> layer(Digraph<Item<T>> graph) {
        return new DigraphLayerAnalyzer<>(graph).calculateLayer();
    }
}
