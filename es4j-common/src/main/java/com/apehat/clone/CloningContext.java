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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class CloningContext {

    private Set<Clone> clones = new LinkedHashSet<>();

    private static final Set<Class<?>> VALUE_CLASSES;

    static {
        Class<?>[] nonStatusClasses = {
            int.class, short.class, boolean.class, byte.class,
            long.class, char.class, float.class, double.class,
            Integer.class, Short.class, Boolean.class, Byte.class,
            Long.class, Character.class, Float.class, Double.class,
            String.class, Object.class
        };
        VALUE_CLASSES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(nonStatusClasses)));
    }

    public static boolean isValueClass(Class<?> cls) {
        return VALUE_CLASSES.contains(cls);
    }

    private static boolean isValueObject(Object object) {
        return object == null || VALUE_CLASSES.contains(object.getClass());
    }

    public void registerClone(Clone clone) {
        this.clones.add(clone);
    }

    public <T> T deepClone(T prototype) {
        if (isValueObject(prototype)) {
            return prototype;
        }
        for (Clone clone : clones) {
            try {
                Value<T> value = clone.deepClone(prototype, this);
                if (value != null) {
                    return value.get();
                }
            } catch (Exception e) {
                // ignore
            }
        }
        throw new IllegalStateException(prototype + " is not be supported to clone");
    }
}
