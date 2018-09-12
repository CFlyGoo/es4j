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

import static org.testng.Assert.assertEqualsDeep;
import static org.testng.Assert.assertNotEqualsDeep;
import static org.testng.AssertJUnit.assertNotSame;
import static org.testng.AssertJUnit.assertNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class MapCloneTest {

    private Clone clone = new MapClone();
    private CloningService service = new DefaultCloningService();

    @Test(expectedExceptions = NotSupportedCloneException.class)
    public void testDeepCloneWithNullService() {
        clone.deepClone(new HashMap<>(), null);
    }

    @Test
    public void testDeepCloneWithNull() {
        assertNull(clone.deepClone(null, service));
    }

    @Test(expectedExceptions = NotSupportedCloneException.class)
    public void testDeepCloneWithNonMap() {
        assertNull(clone.deepClone(new Object(), service));
    }

    @Test
    public void testDeepCloneMultidimensionalMap() {
        Map<Set<String>, Set<Integer>> prototype = new HashMap<>();

        Set<String> firstKey = new HashSet<>();
        firstKey.add("1");
        firstKey.add("2");
        firstKey.add("3");
        Set<Integer> firstValue = new HashSet<>();
        firstValue.add(1);
        firstValue.add(2);
        firstValue.add(3);

        Set<String> secondKey = new HashSet<>();
        secondKey.add("4");
        secondKey.add("5");
        secondKey.add("6");
        Set<Integer> secondValue = new HashSet<>();
        secondValue.add(4);
        secondValue.add(5);
        secondValue.add(6);

        prototype.put(firstKey, firstValue);
        prototype.put(secondKey, secondValue);

        Map<Set<String>, Set<Integer>> cloneValue = clone.deepClone(prototype, service);

        assertEqualsDeep(prototype, cloneValue);
        assertNotSame(prototype, cloneValue);

        firstValue.add(7);
        secondValue.add(8);
        assertNotEqualsDeep(prototype, cloneValue);
    }
}