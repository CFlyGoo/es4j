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

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.AssertJUnit.assertNull;

import java.util.Arrays;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ArrayCloneTest {

    private Clone clone = new ArrayClone();
    private CloningService service = new DefaultCloningService();

    @Test(expectedExceptions = NotSupportedCloneException.class)
    public void testDeepCloneWithNullService() {
        int[] prototype = {1, 2, 3, 4, 5};
        clone.deepClone(prototype, null);
    }

    @Test(expectedExceptions = NotSupportedCloneException.class)
    public void testDeepCloneWithNonArray() {
        clone.deepClone(new Object(), service);
    }

    @Test
    public void testDeepCloneWithNull() {
        assertNull(clone.deepClone(null, service));
    }

    @Test
    public void testDeepCloneArray() {
        int[] prototype = {1, 2, 3, 4, 5};
        int[] cloneValue = clone.deepClone(prototype, service);
        Assert.assertEquals(prototype, cloneValue);
        assertNotSame(prototype, cloneValue);
    }

    @Test
    public void testDeepCloneMultidimensionalArrays() {
        char[][] prototype = {
            {'a', 'b', 'c'},
            {'d', 'e', 'f'}
        };
        char[][] cloneValue = clone.deepClone(prototype, service);
        assert Arrays.deepEquals(prototype, cloneValue);
        assertNotSame(prototype, cloneValue);
        prototype[0][1] = 'g';
        assertNotEquals(prototype, cloneValue);
    }
}