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

import java.util.Objects;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class Layer<E> {

    private final int index;
    private final Set<E> items;
    private Layer<E> nextLayer;

    public Layer(int index, Set<E> items) {
        this.index = index;
        this.items = Objects.requireNonNull(items, "items must not be null");
    }

    public void add(Layer<E> layer) {
        if (nextLayer == null) {
            nextLayer = layer;
        } else {
            nextLayer.add(layer);
        }
    }

    public Set<E> itemsIn(int index) {
        if (index == this.index) {
            return getItems();
        }
        if (nextLayer == null) {
            throw new IllegalArgumentException("Don't have " + index + " layer");
        }
        return nextLayer.itemsIn(index);
    }

    public int getLayerIndex(E value) {
        for (E item : items) {
            if (item.equals(value)) {
                return index;
            }
        }
        if (nextLayer == null) {
            throw new IllegalArgumentException("Cannot find " + value);
        }
        return nextLayer.getLayerIndex(value);
    }

    public Set<E> getItems() {
        return items;
    }

    public int count() {
        if (nextLayer == null) {
            return index;
        }
        return nextLayer.count();
    }
}
