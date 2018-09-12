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

package com.apehat.clone;

import com.apehat.util.ClassUtils;
import com.apehat.util.ReflectionUtils;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class CollectionClone extends AbstractClone {

    @Override
    protected <T> T doDeepClone(T prototype, CloningService service) {
        Objects.requireNonNull(service, "Must specify a CloningService");
        T newInstance = ReflectionUtils.newInstance(ClassUtils.getParameterizedClass(prototype));
        final Collection container = (Collection) newInstance;
        ((Collection<?>) prototype).forEach((Consumer<Object>) o -> {
            //noinspection unchecked - safe
            container.add(service.deepClone(o));
        });
        return newInstance;
    }

    @Override
    protected boolean isCloneable(Class<?> cls) {
        return Collection.class.isAssignableFrom(cls);
    }
}
