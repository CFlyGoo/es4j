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

package com.apehat.util.layer;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface Layer<E> {

    Set<E> currentLayerItems();

    int index();

    void addAsFloor(Set<E> e);

    Layer<E> pre();

    Layer<E> next();

    default int count() {
        return next() == null ? index() + 1 : next().count();
    }

    default int layerOf(E e) {
        if (currentLayerItems().contains(e)) {
            return index();
        }
        if (pre() != null) {
            return pre().layerOf(e);
        }
        if (next() == null) {
            throw new IllegalArgumentException("Cannot find " + e);
        }
        return next().layerOf(e);
    }

    default Set<E> allItems() {
        if (pre() != null) {
            return pre().allItems();
        }
        final Set<E> items = new LinkedHashSet<>();
        Layer<E> currentLayer = this;
        while (currentLayer != null) {
            items.addAll(currentLayer.allItems());
            currentLayer = currentLayer.next();
        }
        return items;
    }

    default Layer<E> getLayer(int i) {
        if (i >= count()) {
            throw new IllegalArgumentException("Only have " + count() + " layer.");
        }
        if (i == index()) {
            return this;
        } else if (i < index()) {
            return pre().getLayer(i);
        } else {
            return next().getLayer(i);
        }
    }

    default Layer<E> top() {
        return getLayer(0);
    }

    default Layer<E> floor() {
        return getLayer(count() - 1);
    }
}
