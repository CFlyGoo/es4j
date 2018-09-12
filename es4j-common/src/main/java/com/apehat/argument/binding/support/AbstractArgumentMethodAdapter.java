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

package com.apehat.argument.binding.support;

import com.apehat.NestedCheckException;
import com.apehat.Value;
import com.apehat.argument.binding.ArgumentAdapter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author hanpengfei
 * @since 1.0
 */
public abstract class AbstractArgumentMethodAdapter implements ArgumentAdapter {

    @Override
    public final Value<?> adapt(String alias, Object prototype) {
        if (prototype == null) {
            return null;
        }
        assert !alias.isEmpty() && alias.lastIndexOf(SEPARATOR) == -1;

        final Class<?> prototypeClass = prototype.getClass();
        Method candidateMethod = null;
        for (Method method : prototypeClass.getDeclaredMethods()) {
            if (isAdaptable(alias, method)) {
                if (candidateMethod != null) {
                    throw new IllegalStateException("Find multiple method with " + alias);
                }
                candidateMethod = method;
            }
        }
        if (candidateMethod != null) {
            candidateMethod.setAccessible(true);
            // if method need parameter, will throws Exception
            try {
                return new Value<>(candidateMethod.invoke(prototype));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new NestedCheckException(e);
            }
        }
        return null;
    }

    protected abstract boolean isAdaptable(String alias, Method method);
}
