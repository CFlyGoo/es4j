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
public final class AdjacencyDigraph<E> implements Digraph<E> {

    private final List<Vertex<E>> vertices;
    private final Indicator<? super E> indicator;

    public AdjacencyDigraph(Set<E> vertices, Indicator<? super E> indicator) {
        if (vertices == null || vertices.size() == 0) {
            throw new IllegalArgumentException("Must specified vertices");
        }
        this.indicator = Objects.requireNonNull(indicator, "Indicator must not be null");
        ArrayList<Vertex<E>> temp = new ArrayList<>();
        for (E node : vertices) {
            temp.add(new Vertex<>(node));
        }
        this.vertices = Collections.unmodifiableList(temp);
        initAdjacencyList();
    }

    @Override
    public Set<E> getAdjacentFirstVertices(E node) {
        return Vertex.values(this.vertices.get(indexOf(node)).adjacentInVertices());
    }

    @Override
    public Set<E> getAdjacentReachableVertices(E vertex) {
        return Vertex.values(this.vertices.get(indexOf(vertex)).adjacentOutVertices());
    }

    @Override
    public Set<E> vertices() {
        return Vertex.values(new HashSet<>(vertices));
    }

    @Override
    public Set<E> getReachableVertices(E node) {
        return Vertex.values(vertices.get(indexOf(node)).outVertices());
    }

    @Override
    public Set<E> getFirstVertices(E node) {
        return Vertex.values(vertices.get(indexOf(node)).inVertices());
    }

    @Override
    public boolean isDirected(E head, E tail) {
        return head.equals(tail) || indicator.isDirection(head, tail);
    }

    private int indexOf(E node) {
        for (int idx = 0; idx < this.vertices.size(); idx++) {
            if (vertices.get(idx).value().equals(node)) {
                return idx;
            }
        }
        throw new IllegalArgumentException("Cannot find " + node);
    }

    private void initAdjacencyList() {
        for (Vertex<E> head : this.vertices) {
            for (Vertex<E> tail : this.vertices) {
                if (!head.equals(tail) && isDirected(tail.value(), head.value())) {
                    final Edge<E> edge = new Edge<>(head, tail);
                    head.addInEdge(edge);
                    tail.addOutEdge(edge);
                }
            }
        }
    }

    private static final class Edge<E> {

        private final Vertex<E> head;
        private final Vertex<E> tail;

        Edge(Vertex<E> head, Vertex<E> tail) {
            this.head = Objects.requireNonNull(head, "head must not be null");
            this.tail = Objects.requireNonNull(tail, "tail most not be null");
        }

        Vertex<E> adjacentVertexOf(Vertex<E> vertex) {
            if (head().equals(vertex)) {
                return tail();
            }
            if (tail().equals(vertex)) {
                return head();
            }
            throw new IllegalArgumentException(this + " hadn't incident " + vertex);
        }

        Vertex<E> head() {
            return head;
        }

        Vertex<E> tail() {
            return tail;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Edge<?> edge = (Edge<?>) o;
            return Objects.equals(head, edge.head) &&
                Objects.equals(tail, edge.tail);
        }

        @Override
        public int hashCode() {
            return Objects.hash(head, tail);
        }
    }

    private static final class Vertex<E> {

        private final E value;
        private Set<Edge<E>> inEdges;
        private Set<Edge<E>> outEdges;

        static <E> Set<E> values(Set<Vertex<E>> vertices) {
            final Set<E> values = new LinkedHashSet<>();
            for (Vertex<E> vertex : vertices) {
                values.add(vertex.value());
            }
            return values;
        }

        private Vertex(E value) {
            this.value = Objects.requireNonNull(value, "Vertex value must not be null");
            this.inEdges = new HashSet<>();
            this.outEdges = new HashSet<>();
        }

        void addOutEdge(Edge<E> edge) {
            this.outEdges.add(edge);
        }

        void addInEdge(Edge<E> edge) {
            this.inEdges.add(edge);
        }

        E value() {
            return value;
        }

        Set<Vertex<E>> adjacentInVertices() {
            return adjacencies(inEdges);
        }

        Set<Vertex<E>> adjacentOutVertices() {
            return adjacencies(outEdges);
        }

        private Set<Vertex<E>> adjacencies(Set<Edge<E>> edges) {
            final Set<Vertex<E>> adjacencies = new HashSet<>();
            for (Edge<E> edge : edges) {
                adjacencies.add(edge.adjacentVertexOf(this));
            }
            adjacencies.add(this);
            return adjacencies;
        }

        Set<Vertex<E>> inVertices() {
            final Set<Vertex<E>> adjacencies = adjacentInVertices();
            final Set<Vertex<E>> temp = new HashSet<>();
            for (Vertex<E> vertex : adjacencies) {
                if (!this.equals(vertex)) {
                    temp.addAll(vertex.inVertices());
                }
            }
            adjacencies.addAll(temp);
            return adjacencies;
        }

        Set<Vertex<E>> outVertices() {
            final Set<Vertex<E>> adjacencies = adjacentOutVertices();
            final Set<Vertex<E>> temp = new HashSet<>();
            for (Vertex<E> vertex : adjacencies) {
                if (!this.equals(vertex)) {
                    temp.addAll(vertex.outVertices());
                }
            }
            adjacencies.addAll(temp);
            return adjacencies;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Vertex acme = (Vertex) o;
            return Objects.equals(value, acme.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
