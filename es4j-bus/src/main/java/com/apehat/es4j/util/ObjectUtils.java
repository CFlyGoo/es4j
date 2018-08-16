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

import com.apehat.es4j.NestedCheckException;
import com.apehat.es4j.NestedIOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class ObjectUtils {

    private ObjectUtils() {
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean isValueObject(Object object) {
        return object == null || ClassUtils.isNonStatusClass(object.getClass());
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
                //noinspection unchecked - safe
                return (T) new DefaultDeserializer().deserialize(bais);
            }
        } catch (IOException e) {
            throw new NestedIOException(e);
        }
    }

    private static <T> T plainObjectDeepClone(T prototype) {
        final Class<T> prototypeClass = ClassUtils.getParameterizedClass(prototype);
        final T clone = newInstance(prototypeClass);
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
        final T newInstance = newInstance(prototypeClass);
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
        final T newInstance = newInstance(prototypeClass);
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
        if (isValueObject(componentType)) {
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

    private static <T> T newInstance(Class<T> cls, Object... args) {
        Class<?>[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }
        try {
            Constructor<T> constructor = cls.getConstructor(paramTypes);
            return ReflectionUtils.access(constructor, accessible -> {
                try {
                    return constructor.newInstance(args);
                } catch (InstantiationException | InvocationTargetException e) {
                    throw new NestedCheckException(e);
                }
            });
        } catch (NoSuchMethodException e) {
            throw new NestedCheckException(e);
        }
    }


}
