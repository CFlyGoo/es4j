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

import com.apehat.es4j.common.NestedCheckException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

/**
 * @author hanpengfei
 * @since 1.0
 */
@SuppressWarnings("WeakerAccess")
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static <T extends AccessibleObject, R> R access(T object, AccessFunction<T, R> fn) {
        final boolean flag = toAccessible(object);
        try {
            return fn.access(object);
        } catch (IllegalAccessException e) {
            throw new NestedCheckException(e);
        } finally {
            object.setAccessible(flag);
        }
    }

    public static boolean toAccessible(AccessibleObject object) {
        boolean accessible = object.isAccessible();
        if (!accessible) {
            object.setAccessible(true);
        }
        return accessible;
    }

    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    public static boolean isFinal(Member member) {
        return Modifier.isFinal(member.getModifiers());
    }

    public static void setFieldValue(Field field, Object instance, Object value) {
        ReflectionUtils.access(field, (AccessFunction<Field, Void>) accessible -> {
            makeNonFinal(field);
            accessible.set(instance, value);
            return null;
        });
    }

    public static void makeNonFinal(Member member) {
        if (isFinal(member)) {
            try {
                ReflectionUtils.access(
                    member.getClass().getDeclaredField("modifiers"),
                    (AccessFunction<Field, Void>) mf -> {
                        mf.set(member, member.getModifiers() & ~Modifier.FINAL);
                        return null;
                    });
            } catch (NoSuchFieldException e) {
                // will not happen
                throw new NestedCheckException(e);
            }
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
