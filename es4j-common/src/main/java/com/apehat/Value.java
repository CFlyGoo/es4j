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

package com.apehat;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class Value<T> {

    private static final Value<?> EMPTY = new Value<>(null);

    public static Value empty() {
        return EMPTY;
    }

    private final T prototype;

    public Value(T prototype) {
        this.prototype = prototype;
    }

    public T get() {
        return prototype;
    }
}
