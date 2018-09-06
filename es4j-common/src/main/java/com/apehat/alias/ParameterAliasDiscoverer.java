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

package com.apehat.alias;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface ParameterAliasDiscoverer extends AliasDiscoverer<Parameter> {

    String getAlias(Parameter param);

    default String getAlias(Executable exec, int index) {
        return getAlias(exec.getParameters()[index]);
    }

    default String[] getAlias(Executable exec) {
        final int count = exec.getParameterCount();
        final String[] aliases = new String[count];
        for (int i = 0; i < count; i++) {
            aliases[i] = getAlias(exec, i);
        }
        return aliases;
    }
}