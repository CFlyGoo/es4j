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

import static org.testng.Assert.assertEquals;

import com.apehat.es4j.bus.support.EventHandleMethodProvider;
import java.util.Date;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class AsmParameterNameDiscovererTest {

    public AsmParameterNameDiscovererTest() {
    }

    private AsmParameterNameDiscovererTest(String str, int i, Date date) {

    }

    @Test
    public void testGetParameterNames() {
        final String[] exceptedNames = EventHandleMethodProvider.getHandlerParameterNames();
        String[] parameterNames = new AsmParameterNameDiscoverer()
            .getParameterNames(new EventHandleMethodProvider().getHandleMethod());
        assertEquals(parameterNames, exceptedNames);
    }

    @Test
    public void testGetParameterNamesWithConstructor() throws Exception {
        final String[] exceptedNames = {"str", "i", "date"};
        String[] parameterNames = new AsmParameterNameDiscoverer()
            .getParameterNames(AsmParameterNameDiscovererTest.class
                .getDeclaredConstructor(String.class, int.class, Date.class));
        assertEquals(exceptedNames, parameterNames);
    }
}