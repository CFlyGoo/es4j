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

import java.lang.reflect.Method;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class IsNameAdapter extends AbstractArgumentMethodAdapter {

    @Override
    protected boolean isAdaptable(String alias, Method method) {
        final Class<?> returnType = method.getReturnType();
        if (returnType != boolean.class && returnType != Boolean.class) {
            return false;
        }
        String getter = "is" + alias.substring(0, 1).toUpperCase() + alias.substring(1);
        return getter.equals(method.getName()) && method.getParameterCount() == 0;
    }
}
