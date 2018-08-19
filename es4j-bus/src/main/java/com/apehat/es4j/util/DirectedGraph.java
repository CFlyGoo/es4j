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

import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface DirectedGraph<E> {

    Set<E> getReachableSet(E item);

    Set<E> getFirstSet(E item);

    boolean isAdjacent(E head, E tail);

    boolean isReachable(E head, E tail);
}
