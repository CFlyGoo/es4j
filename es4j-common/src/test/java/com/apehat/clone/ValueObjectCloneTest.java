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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ValueObjectCloneTest {

    private Clone clone = new ValueObjectClone();
    // CloningService is null-able
    private CloningService service = null;

    @Test
    public void testDeepCloneWithNull() {
        assertNull(clone.deepClone(null, service));
    }

    @Test(expectedExceptions = NotSupportedCloneException.class)
    public void testDeepCloneWithNonValueObject() {
        assertNull(clone.deepClone(new NonValueObject(), service));
    }

    @Test
    public void testDeepCloneWithValueObject() {
        String s = "ValueObject";
        String cloneValue = clone.deepClone(s, service);
        assertNotNull(cloneValue);
        assertSame(s, cloneValue);
    }

    private static class NonValueObject {

        private Class<?> cls = NonValueObject.class;
    }
}