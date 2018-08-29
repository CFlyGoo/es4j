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

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ReflectionParameterAliasDiscoverer implements ParameterAliasDiscoverer {

    @Override
    public String[] getParameterAlias(Executable exec) {
        final int count = exec.getParameterCount();
        if (count == 0) {
            return new String[0];
        }
        final Parameter[] parameters = exec.getParameters();
        if (!parameters[0].isNamePresent()) {
            return new String[0];
        }
        String[] paramNames = new String[count];
        for (int i = 0; i < count; i++) {
            paramNames[i] = parameters[i].getName();
        }
        return paramNames;
    }
}