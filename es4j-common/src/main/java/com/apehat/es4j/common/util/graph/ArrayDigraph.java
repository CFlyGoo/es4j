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

import com.apehat.es4j.common.util.MatrixUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ArrayDigraph<E> implements Digraph<E> {

    private final List<E> vertices;
    private final Indicator<? super E> indicator;

    private transient byte[][] adjacencyMatrix;
    private transient byte[][] reachableMatrix;
    private transient Integer[][] reachableSet;
    private transient Integer[][] firstSet;

    public ArrayDigraph(Set<E> vertices, Indicator<? super E> indicator) {
        if (vertices == null || vertices.size() == 0) {
            throw new IllegalArgumentException("Must specified vertices");
        }
        this.indicator = Objects.requireNonNull(indicator, "Indicator must not be null");
        this.vertices = new ArrayList<>(vertices);
    }

    @Override
    public Set<E> getAdjacentFirstVertices(E node) {
        byte[][] adjacencyMatrix = getAdjacencyMatrix();
        final Set<E> vertices = new HashSet<>();
        final int idx = this.vertices.indexOf(node);
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            if (adjacencyMatrix[i][idx] == 1) {
                vertices.add(this.vertices.get(i));
            }
        }
        return vertices;
    }

    @Override
    public Set<E> getAdjacentReachableVertices(E vertex) {
        byte[][] adjacencyMatrix = getAdjacencyMatrix();
        final byte[] adjacencyList = adjacencyMatrix[this.vertices.indexOf(vertex)];
        final Set<E> vertices = new HashSet<>();
        for (int i = 0; i < adjacencyList.length; i++) {
            if (adjacencyList[i] == 1) {
                vertices.add(this.vertices.get(i));
            }
        }
        return vertices;
    }

    @Override
    public Set<E> getReachableVertices(E item) {
        return indexOf(getReachableSet(this.vertices.indexOf(item)));
    }

    @Override
    public Set<E> getFirstVertices(E item) {
        return indexOf(getFirstSet(this.vertices.indexOf(item)));
    }

    @Override
    public boolean isDirected(E from, E to) {
        return from.equals(to) || indicator.isDirection(from, to);
    }

    @Override
    public Set<E> vertices() {
        return new HashSet<>(vertices);
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
        if (adjacencyMatrix == null) {
            final int length = this.vertices.size();
            final byte[][] matrix = new byte[length][length];
            for (int from = 0; from < length; from++) {
                for (int to = 0; to < length; to++) {
                    if (isDirected(from, to)) {
                        matrix[from][to] = 1;
                    }
                }
            }
            this.adjacencyMatrix = matrix;
        }
        return adjacencyMatrix;
    }

    private Integer[] getFirstSet(int idx) {
        if (idx == -1) {
            throw new IllegalArgumentException("Non item in " + -1);
        }
        if (this.firstSet == null) {
            this.firstSet = new Integer[this.vertices.size()][];
        }
        if (this.firstSet[idx] == null) {
            final int length = this.vertices.size();
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
            this.reachableSet = new Integer[this.vertices.size()][];
        }
        if (this.reachableSet[idx] == null) {
            final int length = this.vertices.size();
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

    private Set<E> indexOf(Integer[] idxs) {
        final HashSet<E> result = new HashSet<>();
        for (Integer idx : idxs) {
            result.add(this.vertices.get(idx));
        }
        return result;
    }

    private boolean isDirected(int from, int to) {
        return vertices.get(from).equals(vertices.get(to)) ||
            indicator.isDirection(vertices.get(from), vertices.get(to));
    }
}
