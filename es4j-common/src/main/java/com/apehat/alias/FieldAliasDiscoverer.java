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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface FieldAliasDiscoverer extends AliasDiscoverer<Field> {

//    @Override
//    default String getAlias(Field obj) {
//        return this.getAlias(obj);
//    }

    String getAlias(Field field);

    default String getAlias(Field field, String prefix) {
        final String alias = getAlias(field);
        return alias == null ? null : prefix + SEPARATOR + alias;
    }

    default Map<Field, String> getFieldAliases(Class<?> cls) {
        final Map<Field, String> aliases = new HashMap<>();
        for (Field field : cls.getDeclaredFields()) {
            aliases.put(field, getAlias(field));
        }
        return aliases;
    }

    default Map<Field, String> getFieldAliases(Class<?> cls, String prefix) {
        final Map<Field, String> aliases = new HashMap<>();
        for (Field field : cls.getDeclaredFields()) {
            aliases.put(field, getAlias(field, prefix));
        }
        return aliases;
    }
}
