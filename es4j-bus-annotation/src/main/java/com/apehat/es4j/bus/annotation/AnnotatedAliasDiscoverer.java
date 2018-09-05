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

package com.apehat.es4j.bus.annotation;

import com.apehat.es4j.common.alias.FieldAliasDiscoverer;
import com.apehat.es4j.common.alias.ParameterAliasDiscoverer;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class AnnotatedAliasDiscoverer implements ParameterAliasDiscoverer, FieldAliasDiscoverer {

    @Override
    public String getFieldAlias(Field field) {
        return getAlias(field);
    }

    @Override
    public String getParameterAlias(Parameter param) {
        return getAlias(param);
    }

    private String getAlias(AnnotatedElement element) {
        Alias annotation = element.getDeclaredAnnotation(Alias.class);
        return annotation == null || annotation.value().isEmpty() ? null : annotation.value();
    }
}
