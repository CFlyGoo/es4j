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
import com.apehat.clone.Clone;
import com.apehat.clone.CloningContext;
import com.apehat.util.ClassUtils;
import com.apehat.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class ReflectionClone implements Clone {

    @Override
    public <T> Value<T> deepClone(T prototype, CloningContext context) {
        final Class<T> prototypeClass = ClassUtils.getParameterizedClass(prototype);
        final T clone = ReflectionUtils.newInstance(prototypeClass);
        final Field[] fields = prototypeClass.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                Object cloneValue = ReflectionUtils.access(field, f -> {
                    final Object prototypeValue = f.get(prototype);
                    return context.deepClone(prototypeValue);
                });
                ReflectionUtils.setFieldValue(field, clone, cloneValue);
            }
        }
        return new Value<>(clone);
    }
}
