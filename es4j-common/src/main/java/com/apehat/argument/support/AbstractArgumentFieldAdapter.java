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

package com.apehat.argument.support;

import com.apehat.argument.ArgumentAdapter;
import com.apehat.NestedCheckException;
import com.apehat.Value;
import java.lang.reflect.Field;

/**
 * @author hanpengfei
 * @since 1.0
 */
public abstract class AbstractArgumentFieldAdapter implements ArgumentAdapter {

    @Override
    public final Value<?> adapt(String alias, Object prototype) {
        if (prototype == null) {
            return null;
        }
        assert !alias.isEmpty() && alias.lastIndexOf(SEPARATOR) == -1;

        final Class<?> prototypeClass = prototype.getClass();
        Field candidateField = null;
        for (Field field : prototypeClass.getDeclaredFields()) {
            if (isAdaptable(alias, field)) {
                if (candidateField != null) {
                    throw new IllegalStateException("Find multiple field with " + alias);
                }
                candidateField = field;
            }
        }
        if (candidateField != null) {
            candidateField.setAccessible(true);
            try {
                return new Value<>(candidateField.get(prototype));
            } catch (IllegalAccessException e) {
                throw new NestedCheckException(e);
            }
        }
        return null;
    }

    protected abstract boolean isAdaptable(String alias, Field field);
}