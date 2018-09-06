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
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class CollectionClone implements Clone {

    @Override
    public <T> Value<T> deepClone(T prototype, CloningContext context) {
        if (!(prototype instanceof Collection)) {
            return null;
        }
        Class<T> prototypeClass = ClassUtils.getParameterizedClass(prototype);
        final Collection<?> collection = (Collection<?>) prototype;
        final T newInstance = ReflectionUtils.newInstance(prototypeClass);
        Collection container = (Collection) newInstance;
        collection.forEach((Consumer<Object>) o -> {
            Object cloneValue = context.deepClone(o);
            //noinspection unchecked - safe
            container.add(cloneValue);
        });
        return new Value<>(newInstance);
    }
}
