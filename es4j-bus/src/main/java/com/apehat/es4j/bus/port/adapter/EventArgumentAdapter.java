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

package com.apehat.es4j.bus.port.adapter;

import com.apehat.es4j.bus.event.Event;
import com.apehat.es4j.common.Value;
import com.apehat.es4j.common.argument.support.DefaultArgumentAdapter;
import com.apehat.es4j.common.argument.support.PrioritizedArgumentAdapter;
import com.apehat.es4j.common.argument.support.AnnotateFieldAdapter;
import com.apehat.es4j.common.argument.support.AnnotatedMethodAdapter;

/**
 * @author hanpengfei
 * @since 1.0
 */
final class EventArgumentAdapter extends PrioritizedArgumentAdapter {

    public EventArgumentAdapter() {
        registerAdapter(new AnnotatedMethodAdapter());
        registerAdapter(new AnnotateFieldAdapter());
        registerAdapter(new DefaultArgumentAdapter());
    }

    @Override
    public Value<?> adapt(String alias, Object prototype) {
        Value<?> value = super.adapt(alias, prototype);
        if (value != null) {
            return value;
        }
        return super.adapt(alias, ((Event) prototype).prototype());
    }
}
