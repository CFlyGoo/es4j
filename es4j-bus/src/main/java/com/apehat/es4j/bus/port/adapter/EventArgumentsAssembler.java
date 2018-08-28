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
import com.apehat.es4j.util.ArgumentExtractor;
import com.apehat.es4j.util.DefaultArgumentsAssembler;
import com.apehat.es4j.util.ParameterNameDiscoverer;
import java.lang.reflect.Parameter;

/**
 * @author hanpengfei
 * @since 1.0
 */
class EventArgumentsAssembler extends DefaultArgumentsAssembler<Event> {

    public EventArgumentsAssembler(ParameterNameDiscoverer parameterNameDiscoverer,
        ArgumentExtractor<Event> eventArgumentExtractor) {
        super(parameterNameDiscoverer, eventArgumentExtractor);
    }

    @Override
    protected Object doAssemble(Event prototype, Parameter parameter) {
        if (parameter.getType() == Event.class) {
            return prototype;
        }
        return super.doAssemble(prototype, parameter);
    }
}