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

    private final List<Acme<E>> acmes;
    private final Indicator<? super E> indicator;

    private Integer layerCount;
    private int currentBuildCompletedLayer;

    public LinkedDirectedGraph(Set<E> nodes, Indicator<? super E> indicator) {
        if (nodes == null || nodes.size() == 0) {
            throw new IllegalArgumentException("Must specified nodes");
        }
        ArrayList<Acme<E>> temp = new ArrayList<>();
        for (E node : nodes) {
            temp.add(new LinkedAcme<>(node));
        }
        this.acmes = Collections.unmodifiableList(temp);
        this.indicator = indicator;
    }

    public Set<E> getAdjacentReachableSet(E node) {
        final Acme<E> currentAcme = this.acmes.get(indexOf(node));
        if (currentAcme.getAdjacentReachableSet().isEmpty()) {
            initAdjacentSet();
        }
        return Collections.unmodifiableSet(currentAcme.getAdjacentReachableSet());
    }

    private void initAdjacentSet() {
        for (Acme<E> start : this.acmes) {
            for (Acme<E> end : this.acmes) {
                if (isDirected(start.getValue(), end.getValue())) {
                    start.getAdjacentReachableSet().add(end.getValue());
                    end.getAdjacentFirstSet().add(start.getValue());
                }
            }
        }
    }

    public Set<E> getAdjacentFirstSet(E node) {
        final Acme<E> currentAcme = this.acmes.get(indexOf(node));
        if (currentAcme.getAdjacentFirstSet().isEmpty()) {
            initAdjacentSet();
        }
        return Collections.unmodifiableSet(currentAcme.getAdjacentFirstSet());
    }

    @Override
    public Set<E> getIn(int layer) {
        if (currentBuildCompletedLayer >= layer) {
            Set<E> layerItems = new HashSet<>();
            for (Acme<E> acme : acmes) {
                if (acme.getLayer() != null && acme.getLayer() == layer) {
                    layerItems.add(acme.getValue());
                }
            }
            return layerItems;
        } else if (getAllPendingBuildItems().isEmpty()) {
            throw new IllegalArgumentException("Out of max layer " + getLayerCount());
        } else {
            calculateLayer();
            return getIn(layer);
        }
    }

    @Override
    public Set<E> items() {
        return Acme.values(new HashSet<>(acmes));
    }

    @Override
    public int getLayerCount() {
        while (layerCount == null) {
            calculateLayer();
        }
        return layerCount;
    }

    @Override
    public int getLayer(E node) {
        Integer layer;
        while ((layer = acmes.get(indexOf(node)).getLayer()) == null) {
            calculateLayer();
        }
        return layer;
    }

    private void calculateLayer() {
        if (layerCount != null) {
            return;
        }
        final BuildInfoSnapshot snapshot = new BuildInfoSnapshot();
        if (snapshot.getPendingAcmes().isEmpty()) {
            layerCount = currentBuildCompletedLayer;
            return;
        }
        currentBuildCompletedLayer++;
        for (Acme<E> candidate : snapshot.getPendingAcmes()) {
            Set<E> currentReachableSet = new HashSet<>(getReachableSet(candidate.getValue()));
            currentReachableSet.removeAll(snapshot.getCompletedNodes());
            if (getCrossSet(candidate.getValue()).equals(currentReachableSet)) {
                candidate.setLayer(currentBuildCompletedLayer);
            }
        }
    }

    @Override
    public Set<E> getReachableSet(E node) {
        final Acme<E> currentAcme = this.acmes.get(indexOf(node));
        if (currentAcme.getReachableSet().isEmpty()) {
            final Set<E> adjacentReachableSet = getAdjacentReachableSet(currentAcme.getValue());
            for (E adjacentReachableNode : adjacentReachableSet) {
                currentAcme.getReachableSet().add(adjacentReachableNode);
                if (!adjacentReachableNode.equals(currentAcme.getValue())) {
                    currentAcme.getReachableSet().addAll(getReachableSet(adjacentReachableNode));
                }
            }
        }
        return Collections.unmodifiableSet(currentAcme.getReachableSet());
    }

    @Override
    public Set<E> getFirstSet(E node) {
        final Acme<E> currentAcme = this.acmes.get(indexOf(node));
        if (currentAcme.getFirstSet().isEmpty()) {
            for (E adjacentFirstNode : getAdjacentFirstSet(currentAcme.getValue())) {
                currentAcme.getFirstSet().add(adjacentFirstNode);
                if (!adjacentFirstNode.equals(currentAcme.getValue())) {
                    currentAcme.getFirstSet().addAll(getFirstSet(adjacentFirstNode));
                }
            }
        }
        return Collections.unmodifiableSet(currentAcme.getFirstSet());
    }

    @Override
    public boolean isDirected(E head, E tail) {
        //noinspection unchecked - safe
        return head.equals(tail) || ((indicator == null) ?
            ((Directed<E>) head).isDirected(tail) : indicator.isDirection(head, tail));
    }

    private Set<E> getCrossSet(E node) {
        final Set<E> crossSet = new HashSet<>();
        final Set<E> firstSet = getFirstSet(node);
        for (E reachable : getReachableSet(node)) {
            if (firstSet.contains(reachable)) {
                crossSet.add(reachable);
            }
        }
        return crossSet;
    }

    private final class BuildInfoSnapshot {

        private final Set<Acme<E>> pendingAcmes;
        private final Set<Acme<E>> completedAcmes;
        private Set<E> completedNodes;

        public BuildInfoSnapshot() {
            final Set<Acme<E>> completedAcmes = new HashSet<>(acmes);
            if (layerCount != null) {
                this.pendingAcmes = Collections.emptySet();
                this.completedAcmes = completedAcmes;
            } else {
                this.pendingAcmes = getAllPendingBuildItems();
                completedAcmes.removeAll(pendingAcmes);
                this.completedAcmes = completedAcmes;
            }
        }

        public Set<Acme<E>> getPendingAcmes() {
            return pendingAcmes;
        }

        public Set<Acme<E>> getCompletedAcmes() {
            return completedAcmes;
        }

        public Set<E> getCompletedNodes() {
            if (this.completedNodes == null) {
                this.completedNodes = Acme.values(completedAcmes);
            }
            return this.completedNodes;
        }
    }

    private Set<Acme<E>> getAllPendingBuildItems() {
        final Set<Acme<E>> pendingAcmes = new HashSet<>();
        for (Acme<E> currentAcme : this.acmes) {
            if (currentAcme.getLayer() == null) {
                pendingAcmes.add(currentAcme);
            }
        }
        return pendingAcmes;
    }

    private int indexOf(E node) {
        for (int idx = 0; idx < this.acmes.size(); idx++) {
            if (acmes.get(idx).getValue().equals(node)) {
                return idx;
            }
        }
        throw new IllegalArgumentException("Cannot find " + node);
    }


    private static final class LinkedAcme<E> implements Acme<E> {

        private final E value;
        private Integer layer;
        private Set<E> adjacentFirstSet;
        private Set<E> adjacentReachableSet;
        private Set<E> firstSet;
        private Set<E> reachableSet;

        private LinkedAcme(E value) {
            this.value = Objects.requireNonNull(value, "Value must not be null");
            this.adjacentFirstSet = new LinkedHashSet<>();
            this.adjacentReachableSet = new LinkedHashSet<>();
            this.firstSet = new LinkedHashSet<>();
            this.reachableSet = new LinkedHashSet<>();
        }

        @Override
        public void setLayer(Integer layer) {
            if (this.layer != null) {
                throw new IllegalStateException("Already sets layer " + layer);
            }
            this.layer = layer;
        }

        @Override
        public E getValue() {
            return value;
        }

        @Override
        public Integer getLayer() {
            return layer;
        }

        @Override
        public Set<E> getAdjacentFirstSet() {
            return adjacentFirstSet;
        }

        @Override
        public Set<E> getAdjacentReachableSet() {
            return adjacentReachableSet;
        }

        @Override
        public Set<E> getFirstSet() {
            return firstSet;
        }

        @Override
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
            LinkedAcme acme = (LinkedAcme) o;
            return Objects.equals(value, acme.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
