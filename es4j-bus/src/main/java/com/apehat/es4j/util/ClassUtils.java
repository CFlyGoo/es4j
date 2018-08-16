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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
@SuppressWarnings("WeakerAccess")
public final class ClassUtils {

    private static final Set<Class<?>> NON_STATUS_CLASSES;

    static {
        Class<?>[] nonStatusClasses = {
            int.class, short.class, boolean.class, byte.class,
            long.class, char.class, float.class, double.class,
            Integer.class, Short.class, Boolean.class, Byte.class,
            Long.class, Character.class, Float.class, Double.class,
            String.class, Object.class
        };
        NON_STATUS_CLASSES = new HashSet<>(Arrays.asList(nonStatusClasses));
    }

    private ClassUtils() {
    }

    public static <T> Class<T> getParameterizedClass(T object) {
        //noinspection unchecked - safe
        return (Class<T>) object.getClass();
    }

    public static boolean isNonStatusClass(Class<?> cls) {
        Objects.requireNonNull(cls, "Class must not be null");
        if (cls.isArray()) {
            return false;
        }
        if (NON_STATUS_CLASSES.contains(cls)) {
            return true;
        }
        final Field[] fields = cls.getDeclaredFields();
        if (fields != null && fields.length != 0) {
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    return false;
                }
            }
        }
        NON_STATUS_CLASSES.add(cls);
        return true;
    }
}
