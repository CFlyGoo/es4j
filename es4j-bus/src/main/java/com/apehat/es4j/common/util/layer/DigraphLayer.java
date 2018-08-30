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

package com.apehat.es4j.common.util.layer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class DigraphLayer<E> implements Layer<E> {

    private final int index;
    private final Set<E> items;
    private Layer<E> pre;
    private Layer<E> next;

    public DigraphLayer(Set<E> items) {
        this.items = Objects.requireNonNull(items, "vertices must not be null");
        this.index = 0;
    }

    public DigraphLayer(int index, Set<E> items) {
        this.index = index;
        this.items = Objects.requireNonNull(items, "currentLayerItems must not be null");
    }

    @Override
    public void addAsFloor(Set<E> layer) {
        if (next == null) {
            DigraphLayer<E> next = new DigraphLayer<>(count(), layer);
            next.pre = this;
            this.next = next;
        } else {
            next.addAsFloor(layer);
        }
    }

    @Override
    public Set<E> currentLayerItems() {
        return new HashSet<>(items);
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public Layer<E> next() {
        return next;
    }

    @Override
    public Layer<E> pre() {
        return pre;
    }
}
