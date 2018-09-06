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

import com.apehat.util.graph.AdjacencyDigraph;
import com.apehat.util.graph.Digraph;
import com.apehat.util.graph.Indicator;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class ClassItemCombiner {

    private final Set<Item<Class<?>>> items;

    public ClassItemCombiner(Set<Item<Class<?>>> items) {
        this.items = items;
    }

    public Set<Item<Class<?>>> rebuild() {
        return (items == null || items.isEmpty()) ? items :
            new ItemCombiner<>(createNewDirectedGraph()).combine();
    }

    private Indicator<Item<Class<?>>> indicator() {
        return (o1, o2) -> o1.isEnable() == o2.isEnable() && o2.value()
            .isAssignableFrom(o1.value());
    }

    private Digraph<Item<Class<?>>> createNewDirectedGraph() {
        return new AdjacencyDigraph<>(items, indicator());
    }
}
