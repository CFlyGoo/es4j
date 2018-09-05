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

package com.apehat.es4j.common.util.graph;

import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface Digraph<E> {

    Set<E> getAdjacentFirstVertices(E node);

    Set<E> getAdjacentReachableVertices(E vertex);

    Set<E> getReachableVertices(E item);

    Set<E> getFirstVertices(E item);

    Set<E> vertices();

    boolean isDirected(E head, E tail);

    default boolean isReachable(E from, E to) {
        return getReachableVertices(from).contains(to);
    }

    default boolean isAdjacent(E head, E tail) {
        return isDirected(head, tail) || isDirected(tail, head);
    }
}
