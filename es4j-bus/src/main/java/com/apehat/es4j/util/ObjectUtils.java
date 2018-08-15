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

import com.apehat.es4j.NestedIOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class ObjectUtils {

    private static final Set<Class<?>> CLONE_SKIP_CLASSES;

    static {
        Class<?>[] cloneSkipArray = {
            int.class, short.class, boolean.class, byte.class,
            long.class, char.class, float.class, double.class,
            Integer.class, Short.class, Boolean.class, Byte.class,
            Long.class, Character.class, Float.class, Double.class,
            String.class
        };
        CLONE_SKIP_CLASSES = new HashSet<>(Arrays.asList(cloneSkipArray));
    }

    public static <T> T deepClone(T prototype) {
        if (prototype == null) {
            return null;
        }

        final Class<T> prototypeClass = ClassUtils.getParameterizedClass(prototype);
        // optimization for value object
        if (CLONE_SKIP_CLASSES.contains(prototypeClass)) {
            return prototype;
        }

        if (prototypeClass.isArray()) {
            return arrayDeepClone(prototype);
        }

        T newInstance = null;
        try {
            if (prototype instanceof Collection) {
                final Collection<?> collection = (Collection) prototype;
                Constructor<T> constructor = prototypeClass.getConstructor();
                newInstance = constructor.newInstance();
                Collection container = (Collection) newInstance;
                collection.forEach((Consumer<Object>) o -> {
                    Object cloneValue = deepClone(o);
                    //noinspection unchecked - safe
                    container.add(cloneValue);
                });
            } else {
                // plain clone
                if (isNonStatusClass(prototypeClass)) {
                    CLONE_SKIP_CLASSES.add(prototypeClass);
                    return prototype;
                }

                // new instance
                Constructor<T> constructor = prototypeClass.getConstructor();
                boolean accessible = ReflectionUtils.toAccessible(constructor);
                try {
                    Field[] fields = prototypeClass.getDeclaredFields();
                    newInstance = constructor.newInstance();
                    for (Field field : fields) {
                        accessible = ReflectionUtils.toAccessible(field);
                        try {
                            final Object prototypeValue = field.get(prototype);
                            Object cloneValue = deepClone(prototypeValue);
                            // TODO static final field will throw IllegalAccessException
                            field.set(newInstance, cloneValue);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            // will not happen
                        } finally {
                            field.setAccessible(accessible);
                        }
                    }
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    // ignore - fallback
                } finally {
                    constructor.setAccessible(accessible);
                }
            }
        } catch (Exception e) {
            // fallback to serialize
            if (prototype instanceof Serializable) {
                newInstance = serialize(prototype);
            }
        }
        if (newInstance == null) {
            throw new IllegalStateException("Unsupported deep clone " + prototype);
        }
        return newInstance;
    }

    private static boolean isNonStatusClass(Class<?> cls) {
        return cls.getDeclaredFields().length == 0;
    }

    private static <T> T arrayDeepClone(T prototype) {
        assert prototype != null;
        final Class<T> prototypeClass = ClassUtils.getParameterizedClass(prototype);
        assert prototypeClass.isArray();
        final int length = Array.getLength(prototype);
        final Class<?> componentType = prototypeClass.getComponentType();
        final T newInstance = prototypeClass.cast(Array.newInstance(componentType, length));
        if (CLONE_SKIP_CLASSES.contains(componentType)) {
            // all component is immutable
            //noinspection SuspiciousSystemArraycopy - safe by check isArray
            System.arraycopy(prototype, 0, newInstance, 0, length);
        } else {
            for (int index = 0; index < length; index++) {
                final Object indexComponent = Array.get(prototype, index);
                final Object cloneComponent = deepClone(indexComponent);
                Array.set(newInstance, index, cloneComponent);
            }
        }
        return newInstance;
    }

    private static <T> T serialize(T prototype) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            new DefaultSerializer().serialize(prototype, baos);
            byte[] bytes = baos.toByteArray();
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                //noinspection unchecked - safe
                return (T) new DefaultDeserializer().deserialize(bais);
            }
        } catch (IOException e) {
            throw new NestedIOException(e);
        }
    }
}
