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

package com.apehat.es4j.bus.support;

import com.apehat.es4j.bus.Type;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class ClassType implements Type {

    private static final long serialVersionUID = 4563762719762323405L;

    private final Class<?> prototype;

    public ClassType(Class<?> prototype) {
        this.prototype = prototype;
    }

    public Class<?> getPrototype() {
        return prototype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClassType classType = (ClassType) o;
        return Objects.equals(prototype, classType.prototype);
    }

    @Override
    public int hashCode() {
        int result = 133;
        result = 31 * result + prototype.hashCode();
        return result;
    }
}
