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
import java.util.LinkedList;
import java.util.List;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class PrioritizedParameterAliasDiscoverer implements ParameterAliasDiscoverer {

    private final List<ParameterAliasDiscoverer> parameterAliasDiscoverers = new LinkedList<>();

    public void registerDiscoverer(ParameterAliasDiscoverer discoverer) {
        this.parameterAliasDiscoverers.add(discoverer);
    }

    @Override
    public String getParameterAlias(Parameter param) {
        for (ParameterAliasDiscoverer discoverer : parameterAliasDiscoverers) {
            String alias = discoverer.getParameterAlias(param);
            if (alias != null) {
                return alias;
            }
        }
        return null;
    }

    @Override
    public String[] getParameterAlias(Executable exec) {
        final int count = exec.getParameterCount();
        final String[] aliases = new String[count];
        final Parameter[] parameters = exec.getParameters();
        for (int i = 0; i < count; i++) {
            aliases[i] = getParameterAlias(parameters[i]);
            if (aliases[i] == null) {
                throw new IllegalStateException("Cannot find parameter" + i + " alias of " + exec);
            }
        }
        return aliases;
    }
}
