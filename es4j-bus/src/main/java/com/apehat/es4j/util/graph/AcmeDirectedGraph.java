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
public final class AcmeDirectedGraph<E> implements DirectedGraph<E> {

    private final List<Acme<E>> acmes;
    private final Indicator<? super E> indicator;

    private Integer layerCount;
    private int currentBuildCompletedLayer;

    public AcmeDirectedGraph(Set<E> nodes, Indicator<? super E> indicator) {
        if (nodes == null || nodes.size() == 0) {
            throw new IllegalArgumentException("Must specified nodes");
        }
        ArrayList<Acme<E>> temp = new ArrayList<>();
        for (E node : nodes) {
            temp.add(new Acme<>(node));
        }
        for (Acme<E> head : temp) {
            for (Acme<E> tail : temp) {
                if (!head.equals(tail) &&
                    indicator.isDirection(head.getValue(), tail.getValue())) {
                    new Extent<>(head, tail, true);
                }
            }
        }
        this.acmes = Collections.unmodifiableList(temp);
        this.indicator = indicator;
    }

    public Set<E> getAdjacentReachableSet(E node) {
        return Acme.values(this.acmes.get(indexOf(node)).getAdjacentReachableSet());
    }

    public Set<E> getAdjacentFirstSet(E node) {
        return Acme.values(this.acmes.get(indexOf(node)).getAdjacentFirstSet());
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
        return Acme.values(new HashSet<>(this.acmes));
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
        return Acme.values(this.acmes.get(indexOf(node)).getReachableSet());
    }

    @Override
    public Set<E> getFirstSet(E node) {
        return Acme.values(this.acmes.get(indexOf(node)).getFirstSet());
    }

    @Override
    public boolean isDirected(E head, E tail) {
        //noinspection unchecked - safe
        return head.equals(tail) ||
            ((indicator == null) ?
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

    private static final class Extent<E> {

        private final Acme<E> head;
        private final Acme<E> tail;
        private final boolean directed;

        private Extent(Acme<E> head, Acme<E> tail, boolean directed) {
            if (head.equals(tail)) {
                throw new IllegalArgumentException("head cannot same as tail");
            }
            this.head = Objects.requireNonNull(head, "head must not be null");
            this.tail = Objects.requireNonNull(tail, "tail must not be null");
            this.directed = directed;
            head.addExtend(this);
            tail.addExtend(this);
        }

        public Acme<E> vertexOf(Acme<E> acme) {
            if (head.equals(acme)) {
                return tail();
            }
            if (tail.equals(acme)) {
                return head();
            }
            throw new IllegalArgumentException("Specified acme don't belong this extent");
        }

        public Acme<E> head() {
            return head;
        }

        public Acme<E> tail() {
            return tail;
        }

        public boolean isDirected() {
            return directed;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Extent extent = (Extent) o;
            return directed == extent.directed &&
                Objects.equals(head, extent.head) &&
                Objects.equals(tail, extent.tail);
        }

        @Override
        public int hashCode() {
            return Objects.hash(head, tail, directed);
        }
    }

    private static final class Acme<E> {

        private Set<Extent<E>> extents;
        private final E value;
        private Integer layer;

        public void addExtend(Extent<E> extent) {
            this.extents.add(extent);
        }

        public Set<Extent<E>> extents() {
            return Collections.unmodifiableSet(this.extents);
        }

        private Acme(E value) {
            this.value = Objects.requireNonNull(value, "Value must not be null");
            this.extents = new HashSet<>();
        }

        public static <E> Set<E> values(Set<Acme<E>> acmes) {
            HashSet<E> values = new LinkedHashSet<>();
            for (Acme<E> acme : acmes) {
                values.add(acme.getValue());
            }
            return values;
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

        public Set<Acme<E>> getAdjacentFirstSet() {
            HashSet<Acme<E>> set = new HashSet<>();
            set.add(this);
            for (Extent<E> extent : extents) {
                if (!extent.isDirected() || this.equals(extent.tail())) {
                    set.add(extent.vertexOf(this));
                }
            }
            return set;
        }

        public Set<Acme<E>> getAdjacentReachableSet() {
            HashSet<Acme<E>> set = new HashSet<>();
            set.add(this);
            for (Extent<E> extent : extents) {
                if (!extent.isDirected() || this.equals(extent.head())) {
                    set.add(extent.vertexOf(this));
                }
            }
            return set;
        }

        public Set<Acme<E>> getFirstSet() {
            final Set<Acme<E>> set = new HashSet<>(getAdjacentFirstSet());
            HashSet<Acme<E>> temp = new HashSet<>();
            for (Acme<E> first : set) {
                if (!this.equals(first)) {
                    temp.addAll(first.getAdjacentFirstSet());
                }
            }
            set.addAll(temp);
            return set;
        }

        public Set<Acme<E>> getReachableSet() {
            final Set<Acme<E>> set = new HashSet<>(getAdjacentReachableSet());
            HashSet<Acme<E>> temp = new HashSet<>();
            for (Acme<E> first : set) {
                if (!this.equals(first)) {
                    temp.addAll(first.getAdjacentReachableSet());
                }
            }
            set.addAll(temp);
            return set;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Acme acme = (Acme) o;
            return Objects.equals(value, acme.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}