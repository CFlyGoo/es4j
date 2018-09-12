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

import com.apehat.Value;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class CloningService {

    private final Set<Clone> clones = new LinkedHashSet<>();

    public void registerClone(Clone clone) {
        this.clones.add(clone);
    }

    public <T> T deepClone(T prototype) {
        for (Clone clone : clones) {
            Value<T> value = clone.deepClone(prototype, this);
            if (value != null) {
                return value.get();
            }
        }
        throw new IllegalStateException(prototype + " is not be supported to clone");
    }
}
