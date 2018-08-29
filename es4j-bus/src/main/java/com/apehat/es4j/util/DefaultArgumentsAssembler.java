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

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class DefaultArgumentsAssembler<T> implements ArgumentsAssembler<T> {

    private final ParameterNameDiscoverer parameterNameDiscoverer;
    private final ArgumentExtractor<T> argumentExtractor;

    public DefaultArgumentsAssembler(
        ParameterNameDiscoverer parameterNameDiscoverer, ArgumentExtractor<T> argumentExtractor) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
        this.argumentExtractor = argumentExtractor;
    }

    @Override
    public Object[] assemble(Executable exec, T prototype) {
        final int count = exec.getParameterCount();
        final ArrayList<Object> args = new ArrayList<>(count);
        for (Parameter parameter : exec.getParameters()) {
            final Object o = doAssemble(prototype, parameter);
            if (o == null && !isNullable(parameter)) {
                throw new IllegalArgumentException(
                    "Argument of " + parameter + " must not be null");
            }
            args.add(o);
        }
        return args.toArray();
    }

    protected Object doAssemble(T prototype, Parameter parameter) {
        final String name = parameterNameDiscoverer.getParameterName(parameter);
        final Value<?> value = argumentExtractor.extract(name, prototype);
        if (value == null && isRequired(parameter)) {
            throw new IllegalStateException("Cannot find " + name + " form " + prototype);
        }
        return value == null ? null : value.get();
    }

    private boolean isRequired(Parameter parameter) {
        return false;
    }

    private boolean isNullable(Parameter parameter) {
        return true;
    }
}
