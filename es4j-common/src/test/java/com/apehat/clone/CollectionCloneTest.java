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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsDeep;
import static org.testng.Assert.assertNotEqualsDeep;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.AssertJUnit.assertNull;

import com.apehat.Value;
import java.util.HashSet;
import java.util.Set;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class CollectionCloneTest {

    private Clone clone = new CollectionClone();
    private CloningService service = new DefaultCloningService();

    @Test(expectedExceptions = NullPointerException.class)
    public void testDeepCloneWithNullService() {
        clone.deepClone(new HashSet<>(), null);
    }

    @Test
    public void testDeepCloneWithNull() {
        Value<Object> value = clone.deepClone(null, service);
        assertNotNull(value);
        assertNull(value.get());
    }

    @Test
    public void testDeepCloneWithNonCollection() {
        assertNull(clone.deepClone(new Object(), service));
    }

    @Test
    public void testDeepCloneCollection() {
        Set<Object> set = new HashSet<>();
        set.add(1);
        set.add(new Object());
        set.add(true);
        Value<Set<Object>> cloneValue = clone.deepClone(set, service);
        assertEquals(set, cloneValue.get());
        assertNotSame(set, cloneValue.get());
    }

    @Test
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

        Value<Set<Set<Integer>>> cloneValue = clone.deepClone(prototype, service);

        assertEqualsDeep(prototype, cloneValue.get(), "Prototype and deepClone is not equals");
        assertNotSame(prototype, cloneValue.get());

        first.add(7);
        second.add(8);
        assertNotEqualsDeep(prototype, cloneValue.get());
    }
}