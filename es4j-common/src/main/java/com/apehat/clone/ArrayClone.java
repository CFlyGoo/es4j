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
import java.lang.reflect.Array;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ArrayClone implements Clone {

    @Override
    public <T> Value<T> deepClone(T prototype, CloningContext context) {
        if (prototype == null) {
            //noinspection unchecked - safe
            return Value.empty();
        }

        final Class<T> prototypeClass = ClassUtils.getParameterizedClass(prototype);
        if (!prototypeClass.isArray()) {
            return null;
        }
        final int length = Array.getLength(prototype);
        final Class<?> componentType = prototypeClass.getComponentType();
        final T newInstance = prototypeClass.cast(Array.newInstance(componentType, length));
        if (ClassUtils.isValueClass(componentType)) {
            //noinspection SuspiciousSystemArraycopy - safe by check isArray
            System.arraycopy(prototype, 0, newInstance, 0, length);
        } else {
            for (int index = 0; index < length; index++) {
                Array.set(newInstance, index, context.deepClone(Array.get(prototype, index)));
            }
        }
        return new Value<>(newInstance);
    }
}
