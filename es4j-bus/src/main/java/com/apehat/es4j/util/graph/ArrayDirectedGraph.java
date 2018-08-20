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

package com.apehat.es4j.util.graph;

import com.apehat.es4j.util.MatrixUtils;
import com.apehat.es4j.util.layer.LayerBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
@SuppressWarnings("WeakerAccess")
public class ArrayDirectedGraph<E> implements DirectedGraph<E> {

    private final List<E> items;
    private final Indicator<? super E> indicator;

    // caches

    private transient byte[][] reachableMatrix;
    private transient Integer[][] reachableSet;
    private transient Integer[][] firstSet;

    public ArrayDirectedGraph(Set<E> items, Indicator<? super E> indicator) {
        if (items == null || items.size() == 0) {
            throw new IllegalArgumentException("Must specified items");
        }
        this.items = new ArrayList<>(items);
        this.indicator = indicator;
    }

    @Override
    public Set<E> getReachableSet(E item) {
        return itemsOf(getReachableSet(this.items.indexOf(item)));
    }

    @Override
    public Set<E> getFirstSet(E item) {
        return itemsOf(getFirstSet(this.items.indexOf(item)));
    }

    @Override
    public boolean isDirected(E head, E tail) {
        return isDirected(this.items.indexOf(head), this.items.indexOf(tail));
    }

    @Override
    public int getLayerCount() {
        return new LayerBuilder<>(this).calculateLayer().indexOf();
    }

    @Override
    public int getLayer(E node) {
        return new LayerBuilder<>(this).calculateLayer().indexOf(node);
    }

    @Override
    public Set<E> getIn(int layer) {
        return new LayerBuilder<>(this).calculateLayer().getAll(layer);
    }

    @Override
    public Set<E> items() {
        return new HashSet<>(items);
    }

    private byte[][] getReachableMatrix() {
        if (this.reachableMatrix == null) {
            final byte[][] adjacencyMatrix = getAdjacencyMatrix();
            final byte[][] reachableMatrix = MatrixUtils.mul(adjacencyMatrix, adjacencyMatrix);
            for (int i = 0; i < reachableMatrix.length; i++) {
                for (int j = 0; j < reachableMatrix.length; j++) {
                    if (reachableMatrix[i][j] > 0) {
                        reachableMatrix[i][j] = 1;
                    }
                }
            }
            this.reachableMatrix = reachableMatrix;
        }
        return this.reachableMatrix;
    }

    private byte[][] getAdjacencyMatrix() {
        final int length = this.items.size();
        final byte[][] matrix = new byte[length][length];
        for (int start = 0; start < length; start++) {
            for (int end = 0; end < length; end++) {
                if (isDirected(start, end)) {
                    matrix[start][end] = 1;
                }
            }
        }
        return matrix;
    }

    private Integer[] getFirstSet(int idx) {
        if (idx == -1) {
            throw new IllegalArgumentException("Non item in " + -1);
        }
        if (this.firstSet == null) {
            this.firstSet = new Integer[this.items.size()][];
        }
        if (this.firstSet[idx] == null) {
            final int length = this.items.size();
            final ArrayList<Integer> firstSet = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                if (getReachableMatrix()[i][idx] == 1) {
                    firstSet.add(i);
                }
            }
            this.firstSet[idx] = firstSet.toArray(new Integer[0]);
        }
        return this.firstSet[idx];
    }

    private Integer[] getReachableSet(int idx) {
        if (idx == -1) {
            throw new IllegalArgumentException("Non item in " + -1);
        }
        if (this.reachableSet == null) {
            this.reachableSet = new Integer[this.items.size()][];
        }
        if (this.reachableSet[idx] == null) {
            final int length = this.items.size();
            ArrayList<Integer> reachableSet = new ArrayList<>(length);
            for (int j = 0; j < length; j++) {
                if (this.getReachableMatrix()[idx][j] == 1) {
                    reachableSet.add(j);
                }
            }
            this.reachableSet[idx] = reachableSet.toArray(new Integer[0]);
        }
        return this.reachableSet[idx];
    }

    private Set<E> itemsOf(Integer[] idxs) {
        final HashSet<E> result = new HashSet<>();
        for (Integer idx : idxs) {
            result.add(this.items.get(idx));
        }
        return result;
    }

    private boolean isDirected(int start, int end) {
        //noinspection unchecked - safe
        return start == end ||
            ((indicator == null) ?
                ((Directed<E>) items.get(start)).isDirected(items.get(end)) :
                indicator.isDirection(items.get(start), items.get(end)));
    }
}
