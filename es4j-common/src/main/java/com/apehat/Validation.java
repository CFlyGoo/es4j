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

package com.apehat;

import com.apehat.util.ReflectionUtils;
import java.lang.reflect.Member;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class Validation {

    private Validation() {
    }

    public static <T extends Member> T requiredStatic(T member) {
        if (!ReflectionUtils.isStatic(member)) {
            throw new IllegalArgumentException(member + " isn't static");
        }
        return member;
    }
}
