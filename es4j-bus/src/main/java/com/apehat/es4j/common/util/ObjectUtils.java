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

import com.apehat.es4j.common.NestedIOException;
import com.apehat.es4j.common.serializer.DefaultDeserializer;
import com.apehat.es4j.common.serializer.DefaultSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class ObjectUtils {

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

    private ObjectUtils() {
    }

    public static <T> T deepClone(T prototype) {
        if (isValueObject(prototype)) {
            return prototype;
        }

        try {
            if (prototype.getClass().isArray()) {
                return arrayDeepClone(prototype);
            }
            if (prototype instanceof Map) {
                return mapDeepClone(prototype);
            }
            if (prototype instanceof Collection) {
                return collectionDeepClone(prototype);
            }
            return plainObjectDeepClone(prototype);
        } catch (Exception e) {
            if (prototype instanceof Serializable) {
                return serialize(prototype);
            }
            throw new IllegalStateException("Unsupported deep clone " + prototype);
        }
    }

    private static <T> T serialize(T prototype) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            new DefaultSerializer().serialize(prototype, baos);
            byte[] bytes = baos.toByteArray();
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                Class<T> cls = ClassUtils.getParameterizedClass(prototype);
                return cls.cast(new DefaultDeserializer().deserialize(bais));
            }
        } catch (IOException e) {
            throw new NestedIOException(e);
        }
    }

    private static <T> T plainObjectDeepClone(T prototype) {
        final Class<T> prototypeClass = ClassUtils.getParameterizedClass(prototype);
        final T clone = ReflectionUtils.newInstance(prototypeClass);
        final Field[] fields = prototypeClass.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                Object cloneValue = ReflectionUtils.access(field, f -> {
                    final Object prototypeValue = f.get(prototype);
                    return deepClone(prototypeValue);
                });
                ReflectionUtils.setFieldValue(field, clone, cloneValue);
            }
        }
        return clone;
    }

    private static <T> T mapDeepClone(T prototype) {
        assert prototype instanceof Map;
        Class<T> prototypeClass = ClassUtils.getParameterizedClass(prototype);
        final T newInstance = ReflectionUtils.newInstance(prototypeClass);
        Map container = (Map) newInstance;
        final Map<?, ?> map = (Map<?, ?>) prototype;
        map.forEach((BiConsumer<Object, Object>) (key, value) -> {
            Object cloneKey = deepClone(key);
            Object cloneValue = deepClone(value);
            //noinspection unchecked - safe
            container.put(cloneKey, cloneValue);
        });
        return newInstance;
    }

    private static <T> T collectionDeepClone(T prototype) {
        assert prototype instanceof Collection;
        Class<T> prototypeClass = ClassUtils.getParameterizedClass(prototype);
        final Collection<?> collection = (Collection<?>) prototype;
        final T newInstance = ReflectionUtils.newInstance(prototypeClass);
        Collection container = (Collection) newInstance;
        collection.forEach((Consumer<Object>) o -> {
            Object cloneValue = deepClone(o);
            //noinspection unchecked - safe
            container.add(cloneValue);
        });
        return newInstance;
    }

    private static <T> T arrayDeepClone(T prototype) {
        assert prototype != null;
        final Class<T> prototypeClass = ClassUtils.getParameterizedClass(prototype);
        assert prototypeClass.isArray();
        final int length = Array.getLength(prototype);
        final Class<?> componentType = prototypeClass.getComponentType();
        final T newInstance = prototypeClass.cast(Array.newInstance(componentType, length));
        if (VALUE_CLASSES.contains(componentType)) {
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

    private static boolean isValueObject(Object object) {
        return object == null || VALUE_CLASSES.contains(object.getClass());
    }

}
