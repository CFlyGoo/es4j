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

package com.apehat.argument.binding.alias;

import static org.testng.Assert.assertEquals;

import com.apehat.support.TestMethodProvider;
import java.util.Date;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class AsmParameterAliasDiscovererTest {

    public AsmParameterAliasDiscovererTest() {
    }

    private AsmParameterAliasDiscovererTest(String str, int i, Date date) {

    }

    @Test
    public void testGetParameterNames() {
        final String[] exceptedNames = TestMethodProvider.getHandlerParameterNames();
        String[] parameterNames = new AsmParameterAliasDiscoverer()
            .getAlias(new TestMethodProvider().getHandleMethod());
        assertEquals(parameterNames, exceptedNames);
    }

    @Test
    public void testGetParameterNamesWithConstructor() throws Exception {
        final String[] exceptedNames = {"str", "i", "date"};
        String[] parameterNames = new AsmParameterAliasDiscoverer()
            .getAlias(AsmParameterAliasDiscovererTest.class
                .getDeclaredConstructor(String.class, int.class, Date.class));
        assertEquals(exceptedNames, parameterNames);
    }
}