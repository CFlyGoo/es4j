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
import static org.testng.Assert.assertEqualsDeep;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotEqualsDeep;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;

import com.apehat.es4j.util.support.NonFieldHaveInnerClass;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ObjectUtilsTest {

    @Test(groups = "deepClone")
    public void testDeepCloneWithNull() {
        Object clone = ObjectUtils.deepClone(null);
        assertNull(clone);
    }

    @Test(groups = "deepClone")
    public void testDeepCloneArray() {
        int[] prototype = {1, 2, 3, 4, 5};
        int[] clone = ObjectUtils.deepClone(prototype);
        assertEquals(prototype, clone);
        assertNotSame(prototype, clone);
    }

    @Test(groups = "deepClone")
    public void testDeepCloneMultidimensionalArrays() {
        char[][] prototype = {
            {'a', 'b', 'c'},
            {'d', 'e', 'f'}
        };
        char[][] clone = ObjectUtils.deepClone(prototype);
        assert Arrays.deepEquals(prototype, clone);
        assertNotSame(prototype, clone);
        prototype[0][1] = 'g';
        assertNotEquals(prototype, clone);
    }

    @Test(groups = "deepClone")
    public void testDeepCloneCollection() {
        Set<Object> set = new HashSet<>();
        set.add(1);
        set.add(new Object());
        set.add(true);
        Set<Object> clone = ObjectUtils.deepClone(set);
        assertEquals(set, clone);
        assertNotSame(set, clone);
    }

    @Test(groups = "deepClone")
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

        Map<Set<String>, Set<Integer>> clone = ObjectUtils.deepClone(prototype);

        assertEqualsDeep(prototype, clone);
        assertNotSame(prototype, clone);

        firstValue.add(7);
        secondValue.add(8);
        assertNotEqualsDeep(prototype, clone);
    }

    @Test(groups = "deepClone")
    public void testDeepCloneMultidimensionalCollection() {
        Set<Set<Integer>> prototype = new HashSet<>();

        Set<Integer> first = new HashSet<>();
        first.add(1);
        first.add(2);
        first.add(3);
        Set<Integer> second = new HashSet<>();
        second.add(4);
        second.add(5);
        second.add(6);

        prototype.add(first);
        prototype.add(second);

        Set<Set<Integer>> clone = ObjectUtils.deepClone(prototype);

        assertEqualsDeep(prototype, clone, "Prototype and clone is not equals");
        assertNotSame(prototype, clone);

        first.add(7);
        second.add(8);
        assertNotEqualsDeep(prototype, clone);
    }

    @Test(groups = "deepClone")
    public void testDeepCloneNonFieldsStatedClass() {
        NonFieldHaveInnerClass prototype = new NonFieldHaveInnerClass();
        final String primitiveName = prototype.getInnerClassName();
        NonFieldHaveInnerClass newInstance = ObjectUtils.deepClone(prototype);
        assertEquals(primitiveName, newInstance.getInnerClassName());

        prototype.changeInnerClassName();
        assertNotEquals(primitiveName, prototype.getInnerClassName());
        assertEquals(newInstance.getInnerClassName(), primitiveName);
    }
}