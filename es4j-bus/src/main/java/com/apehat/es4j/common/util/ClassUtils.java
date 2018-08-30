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

package com.apehat.es4j.common.util;

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

    private ClassUtils() {
    }

    public static <T> Class<T> getParameterizedClass(T object) {
        //noinspection unchecked - safe
        return (Class<T>) object.getClass();
    }

    public static Set<Class<?>> getAllSuperclassesAndInterfaces(Class<?> cls) {
        final Set<Class<?>> supers = getSuperclassAndInterfaces(cls);
        final Set<Class<?>> temp = new HashSet<>();
        for (Class<?> aSuper : supers) {
            temp.addAll(getAllSuperclassesAndInterfaces(aSuper));
        }
        supers.addAll(temp);
        return supers;
    }

    public static Set<Class<?>> getSuperclassAndInterfaces(Class<?> cls) {
        Objects.requireNonNull(cls, "Class must not be null");
        final Set<Class<?>> supers = new HashSet<>();
        if (cls != Object.class && !cls.isInterface()) {
            supers.add(cls.getSuperclass());
        }
        supers.addAll(Arrays.asList((cls.getInterfaces())));
        return supers;
    }
}