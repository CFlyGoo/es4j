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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface Acme<E> {

    static <E> Set<E> values(Set<Acme<E>> acmes) {
        HashSet<E> values = new LinkedHashSet<>();
        for (Acme<E> acme : acmes) {
            values.add(acme.getValue());
        }
        return values;
    }

    void setLayer(Integer layer);

    E getValue();

    Integer getLayer();

    Set<E> getAdjacentFirstSet();

    Set<E> getAdjacentReachableSet();

    Set<E> getFirstSet();

    Set<E> getReachableSet();
}
