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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ReflectionUtils {

    public static <T extends AccessibleObject, R> R access(T object, AccessFunction<T, R> fn) {
        final boolean flag = toAccessible(object);
        try {
            return fn.access(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new NestedCheckException(e);
        } finally {
            object.setAccessible(flag);
        }
    }

    public static int getParameterIndex(Parameter parameter) {
        Parameter[] parameters = parameter.getDeclaringExecutable().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].equals(parameter)) {
                return i;
            }
        }
        // will not happen
        return -1;
    }

    public static void setFieldValue(Field field, Object instance, Object value) {
        ReflectionUtils.access(field, (AccessFunction<Field, Void>) accessible -> {
            try {
                if (Modifier.isFinal(field.getModifiers())) {
                    Field modField = Field.class.getDeclaredField("modifiers");
                    ReflectionUtils.access(modField, (AccessFunction<Field, Void>) mf -> {
                        mf.set(field, field.getModifiers() & ~Modifier.FINAL);
                        return null;
                    });
                }
                assert !Modifier.isFinal(field.getModifiers());
                field.set(instance, value);
            } catch (NoSuchFieldException e) {
                // will not happen
                throw new NestedCheckException(e);
            }
            return null;
        });
    }

    public static boolean toAccessible(AccessibleObject object) {
        boolean accessible = object.isAccessible();
        if (!accessible) {
            object.setAccessible(true);
        }
        return accessible;
    }

    public static <T> T newInstance(Class<T> cls, Object... args) {
        Class<?>[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }
        try {
            Constructor<T> constructor = cls.getDeclaredConstructor(paramTypes);
            return access(constructor, accessible -> {
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
