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

package com.apehat.util.layer;

import com.apehat.util.graph.Digraph;
import java.util.HashSet;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class DigraphLayerAnalyzer<E> {

    private Digraph<E> graph;
    private DigraphLayer<E> firstLayer;

    public DigraphLayerAnalyzer(Digraph<E> graph) {
        this.graph = graph;
    }

    public DigraphLayer<E> calculateLayer() {
        final Set<E> pendingItems = graph.vertices();
        final Set<E> completed = new HashSet<>();

        while (!pendingItems.isEmpty()) {
            Set<E> currentLayerItems = new HashSet<>();
            for (E item : pendingItems) {
                final Set<E> reachableSet = graph.getReachableVertices(item);
                reachableSet.removeAll(completed);
                if (getCrossSet(item).equals(reachableSet)) {
                    currentLayerItems.add(item);
                }
            }
            pendingItems.removeAll(currentLayerItems);
            completed.addAll(currentLayerItems);

            if (firstLayer == null) {
                firstLayer = new DigraphLayer<>(currentLayerItems);
            } else {
                firstLayer.addAsFloor(currentLayerItems);
            }
        }
        return firstLayer;
    }

    private Set<E> getCrossSet(E item) {
        Set<E> firstSet = graph.getFirstVertices(item);
        Set<E> reachableSet = graph.getReachableVertices(item);
        final Set<E> crossSet = new HashSet<>();
        for (E reachable : reachableSet) {
            if (firstSet.contains(reachable)) {
                crossSet.add(reachable);
            }
        }
        return crossSet;
    }
}
