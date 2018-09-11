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

package com.apehat.clone;

import com.apehat.Value;
import com.apehat.util.ClassUtils;
import com.apehat.util.ReflectionUtils;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class MapClone implements Clone {

    @Override
    public <T> Value<T> deepClone(T prototype, CloningService service) {
        Objects.requireNonNull(service, "Must specify a CloningService");
        if (prototype == null) {
            //noinspection unchecked
            return Value.empty();
        }
        if (!(prototype instanceof Map)) {
            return null;
        }
        try {
            T newInstance = ReflectionUtils
                .newInstance(ClassUtils.getParameterizedClass(prototype));
            Map container = (Map) newInstance;
            final Map<?, ?> map = (Map<?, ?>) prototype;
            map.forEach((BiConsumer<Object, Object>) (key, value) -> {
                //noinspection unchecked - safe
                container.put(service.deepClone(key), service.deepClone(value));
            });
            return new Value<>(newInstance);
        } catch (Exception e) {
            return null;
        }
    }
}
