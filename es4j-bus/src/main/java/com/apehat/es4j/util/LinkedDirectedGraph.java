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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
@SuppressWarnings("WeakerAccess")
public final class LinkedDirectedGraph<E> implements DirectedGraph<E> {

    private final List<Item<E>> items;
    private final Indicator<? super E> indicator;

    public LinkedDirectedGraph(Set<E> nodes, Indicator<? super E> indicator) {
        if (nodes == null || nodes.size() == 0) {
            throw new IllegalArgumentException("Must specified nodes");
        }
        ArrayList<Item<E>> temp = new ArrayList<>();
        for (E node : nodes) {
            temp.add(new Item<>(node));
        }
        this.items = Collections.unmodifiableList(temp);
        this.indicator = indicator;
    }

    public Set<E> getAdjacentReachableSet(E node) {
        final int idx = this.indexOf(node);
        if (idx == -1) {
            throw new IllegalArgumentException("Non value in " + -1);
        }
        final Item<E> currentItem = this.items.get(idx);
        if (currentItem.getAdjacentReachableSet().isEmpty()) {
            initAdjacentSet();
        }
        return Collections.unmodifiableSet(currentItem.getAdjacentReachableSet());
    }

    private void initAdjacentSet() {
        for (Item<E> start : this.items) {
            for (Item<E> end : this.items) {
                if (isAdjacent(start.getValue(), end.getValue())) {
                    start.getAdjacentReachableSet().add(end.getValue());
                    end.getAdjacentFirstSet().add(start.getValue());
                }
            }
        }
    }

    public Set<E> getAdjacentFirstSet(E node) {
        final int idx = this.indexOf(node);
        if (idx == -1) {
            throw new IllegalArgumentException("Non value in " + -1);
        }
        final Item<E> currentItem = this.items.get(idx);
        if (currentItem.getAdjacentFirstSet().isEmpty()) {
            initAdjacentSet();
        }
        return Collections.unmodifiableSet(currentItem.getAdjacentFirstSet());
    }

    public int getLayerIndex(E node) {
        final Item item = this.items.get(this.indexOf(node));
        if (item.getLayer() != null) {
            return item.getLayer();
        }

        final Set<Item<E>> pendingItems = getAllPendingBuildItems();
        while (pendingItems.contains(item)) {
            final Set<E> completed = getAllBuildCompletedNodes();
            final int currentLayerIndex = getBuildCompletedLayerIndex();
            for (Item<E> candidate : pendingItems) {
                Set<E> currentReachableSet = new HashSet<>(getReachableSet(candidate.getValue()));
                currentReachableSet.removeAll(completed);
                if (getCrossSet(node).equals(currentReachableSet)) {
                    candidate.setLayer(currentLayerIndex);
                }
            }
        }
        return item.getLayer();
    }

    @Override
    public Set<E> getReachableSet(E node) {
        int idx = this.indexOf(node);
        if (idx == -1) {
            throw new IllegalArgumentException("Non value in " + -1);
        }
        final Item<E> currentItem = this.items.get(idx);
        if (currentItem.getReachableSet().isEmpty()) {
            final Set<E> adjacentReachableSet = getAdjacentReachableSet(currentItem.getValue());
            for (E adjacentReachableNode : adjacentReachableSet) {
                currentItem.getReachableSet().add(adjacentReachableNode);
                if (!adjacentReachableNode.equals(currentItem.getValue())) {
                    currentItem.getReachableSet().addAll(getReachableSet(adjacentReachableNode));
                }
            }
        }
        return Collections.unmodifiableSet(currentItem.getReachableSet());
    }

    @Override
    public Set<E> getFirstSet(E node) {
        final int idx = this.indexOf(node);
        if (idx == -1) {
            throw new IllegalArgumentException("Non value in " + -1);
        }
        final Item<E> currentItem = this.items.get(idx);
        if (currentItem.getFirstSet().isEmpty()) {
            for (E adjacentFirstNode : getAdjacentFirstSet(currentItem.getValue())) {
                currentItem.getFirstSet().add(adjacentFirstNode);
                if (!adjacentFirstNode.equals(currentItem.getValue())) {
                    currentItem.getFirstSet().addAll(getFirstSet(adjacentFirstNode));
                }
            }
        }
        return Collections.unmodifiableSet(currentItem.getFirstSet());
    }

    @Override
    public boolean isAdjacent(E head, E tail) {
        //noinspection unchecked - safe
        return head.equals(tail) || ((indicator == null) ?
            ((Directed<E>) head).isDirected(tail) : indicator.isDirection(head, tail));
    }

    @Override
    public boolean isReachable(E head, E tail) {
        return this.getFirstSet(head).contains(tail);
    }

    private Set<E> getCrossSet(E node) {
        final Set<E> firstSet = getFirstSet(node);
        final Set<E> reachableSet = getReachableSet(node);
        final Set<E> crossSet = new HashSet<>();
        for (E reachable : reachableSet) {
            if (firstSet.contains(reachable)) {
                crossSet.add(reachable);
            }
        }
        return crossSet;
    }

    private Set<E> getAllBuildCompletedNodes() {
        final HashSet<E> completed = new HashSet<>();
        for (Item<E> completedItem : getAllBuildCompletedItems()) {
            completed.add(completedItem.getValue());
        }
        return completed;
    }

    private Set<Item<E>> getAllPendingBuildItems() {
        final Set<Item<E>> pendingItems = new HashSet<>();
        for (Item<E> currentItem : this.items) {
            if (currentItem.getLayer() == null) {
                pendingItems.add(currentItem);
            }
        }
        return pendingItems;
    }

    private int getBuildCompletedLayerIndex() {
        int currentLayerIndex = 0;
        final Set<Item<E>> items = getAllBuildCompletedItems();
        for (Item<E> item : items) {
            if (item.getLayer() > currentLayerIndex) {
                currentLayerIndex = item.getLayer();
            }
        }
        return currentLayerIndex;
    }

    private Set<Item<E>> getAllBuildCompletedItems() {
        final Set<Item<E>> completedItems = new HashSet<>();
        for (Item<E> currentItem : this.items) {
            if (currentItem.getLayer() != null) {
                completedItems.add(currentItem);
            }
        }
        return completedItems;
    }

    private Set<E> getMergeSet(E node) {
        final Set<E> mergeSet = new HashSet<>(getFirstSet(node));
        mergeSet.addAll(getReachableSet(node));
        return mergeSet;
    }

    private int indexOf(E node) {
        for (int idx = 0; idx < this.items.size(); idx++) {
            if (items.get(idx).getValue().equals(node)) {
                return idx;
            }
        }
        throw new IllegalArgumentException("Don't have " + node);
    }

    private static final class Item<E> {

        private final E value;
        private Integer layer;
        private Set<E> adjacentFirstSet;
        private Set<E> adjacentReachableSet;
        private Set<E> firstSet;
        private Set<E> reachableSet;

        private Item(E value) {
            this.value = Objects.requireNonNull(value, "Value must not be null");
            this.adjacentFirstSet = new LinkedHashSet<>();
            this.adjacentReachableSet = new LinkedHashSet<>();
            this.firstSet = new LinkedHashSet<>();
            this.reachableSet = new LinkedHashSet<>();
        }

        public void setLayer(Integer layer) {
            if (this.layer != null) {
                throw new IllegalStateException("Already sets layer " + layer);
            }
            this.layer = layer;
        }

        public E getValue() {
            return value;
        }

        public Integer getLayer() {
            return layer;
        }

        public Set<E> getAdjacentFirstSet() {
            return adjacentFirstSet;
        }

        public Set<E> getAdjacentReachableSet() {
            return adjacentReachableSet;
        }

        public Set<E> getFirstSet() {
            return firstSet;
        }

        public Set<E> getReachableSet() {
            return reachableSet;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            @SuppressWarnings("unchecked") Item item = (Item) o;
            return Objects.equals(value, item.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
