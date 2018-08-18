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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class StructuralModelAnalyzer<E> {

    private final E[] items;
    private final Indicator<E> indicator;

    // caches

    private transient byte[][] reachableMatrix;
    private transient Integer[][] reachableSet;
    private transient Integer[][] firstSet;
    private transient Integer[][] crossSet;
    private transient Integer[][] mergeSet;

    public StructuralModelAnalyzer(Set<? extends Directed<E>> items) {
        if (items == null || items.size() == 0) {
            throw new IllegalArgumentException("Must specified items");
        }
        //noinspection unchecked - safe
        this.items = (E[]) Collections.unmodifiableSet(items).toArray();
        this.indicator = null;
    }

    public StructuralModelAnalyzer(Set<E> items, Indicator<E> indicator) {
        if (items == null || items.size() == 0) {
            throw new IllegalArgumentException("Must specified items");
        }
        //noinspection unchecked - safe
        this.items = (E[]) Collections.unmodifiableSet(items).toArray();
        this.indicator = indicator;
//        getItemsLayer();
    }

    private void getItemsLayer() {
        // 等待计算层级的项目序数
        final Set<Integer> candidates = new HashSet<>();
        for (int i = 0; i < this.items.length; i++) {
            candidates.add(i);
        }
        int layerIdx = 0;
        final Set<Integer> completedItems = new HashSet<>();
        while (!candidates.isEmpty()) {
            layerIdx++;
            Set<Integer> currentLayerItems = new HashSet<>();
            for (Integer candidate : candidates) {
                Set<Integer> cachedReachableSet = new HashSet<>(
                    Arrays.asList(getReachableSet(candidate)));
                cachedReachableSet.removeAll(completedItems);
                Integer[] currentReachableSet = cachedReachableSet.toArray(new Integer[0]);

                if (Arrays.equals(currentReachableSet, getCrossSet(candidate))) {
                    currentLayerItems.add(candidate);
                }
            }
            completedItems.addAll(currentLayerItems);
            candidates.removeAll(currentLayerItems);
        }
    }

    private byte[][] getReachableMatrix() {
        if (this.reachableMatrix == null) {
            final byte[][] adjacencyMatrix = getAdjacencyMatrix();
            final byte[][] reachableMatrix = getMatrixProduct(adjacencyMatrix, adjacencyMatrix);
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

    // 矩阵求和
    private byte[][] getMatrixProduct(byte[][] a, byte[][] b) {
        assert a[0].length == b.length;
        final int rowCount = a.length;
        final int columnCount = b[0].length;
        byte[][] result = new byte[rowCount][columnCount];
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                for (int k = 0; k < b.length; k++) {
                    result[r][c] += a[r][k] * b[k][c];
                }
            }
        }
        return result;
    }

    private byte[][] getAdjacencyMatrix() {
        final int length = this.items.length;
        final byte[][] matrix = new byte[length][length];
        for (int start = 0; start < length; start++) {
            for (int end = 0; end < length; end++) {
                if (isAdjacent(start, end)) {
                    matrix[start][end] = 1;
                }
            }
        }
        return matrix;
    }

    public Set<E> getReachableSet(Class<?> cls) {
        return getItems(getReachableSet(indexOf(cls)));
    }

    public Set<E> getFirstSet(Class<?> cls) {
        return getItems(getFirstSet(indexOf(cls)));
    }

    // 先行集代表是他本身或他的子类
    // 所有该列元素为1的
    private Integer[] getFirstSet(int idx) {
        if (this.firstSet == null) {
            this.firstSet = new Integer[this.items.length][];
        }
        if (this.firstSet[idx] == null) {
            final int length = this.items.length;
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

    // 可达集代表他本身或他的父类
    // 所有该行表示为1的元素
    private Integer[] getReachableSet(int idx) {
        if (this.reachableSet == null) {
            this.reachableSet = new Integer[this.items.length][];
        }
        if (this.reachableSet[idx] == null) {
            final int length = this.items.length;
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

    private Integer[] getMergeSet(int idx) {
        if (this.mergeSet == null) {
            this.mergeSet = new Integer[this.items.length][];
        }
        if (this.mergeSet[idx] == null) {
            final Integer[] firstSet = this.getFirstSet(idx);
            final Integer[] reachableSet = this.getReachableSet(idx);
            Set<Integer> merge = new HashSet<>(Arrays.asList(firstSet));
            merge.addAll(Arrays.asList(reachableSet));
            this.mergeSet[idx] = merge.toArray(new Integer[0]);
        }
        return this.mergeSet[idx];
    }

    private Integer[] getCrossSet(int idx) {
        if (this.crossSet == null) {
            this.crossSet = new Integer[this.items.length][];
        }
        if (this.crossSet[idx] == null) {
            final ArrayList<Integer> crossSet = new ArrayList<>();
            final Set<Integer> firstSetOfIdx = new HashSet<>(Arrays.asList(getFirstSet(idx)));
            final Integer[] reachableSetOfIdx = this.getReachableSet(idx);
            for (final Integer reachableItem : reachableSetOfIdx) {
                if (firstSetOfIdx.contains(reachableItem)) {
                    crossSet.add(reachableItem);
                }
            }
            this.crossSet[idx] = crossSet.toArray(new Integer[0]);
        }
        return this.crossSet[idx];
    }

    private Set<E> getItems(Integer[] idxs) {
        final HashSet<E> result = new HashSet<>();
        for (Integer idx : idxs) {
            result.add(this.items[idx]);
        }
        return result;
    }

    private int indexOf(Class<?> cls) {
        for (int i = 0; i < items.length; i++) {
            if (this.items[i].equals(cls)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Cannot found " + cls);
    }

    private boolean isAdjacent(int start, int end) {
        if (indicator != null) {
            return indicator.isDirected(items[start], items[end]);
        } else {
            assert items[start] instanceof Directed;
            //noinspection unchecked - safe
            return ((Directed<E>) items[start]).isDirected(items[end]);
        }
    }
}
