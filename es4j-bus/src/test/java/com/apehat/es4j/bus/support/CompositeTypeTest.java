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

package com.apehat.es4j.bus.support;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.apehat.es4j.bus.Type;
import com.apehat.es4j.support.sample.SampleClass1;
import com.apehat.es4j.support.sample.SampleClass2;
import com.apehat.es4j.support.sample.SampleClass3;
import com.apehat.es4j.support.sample.SampleMiddleClass1;
import com.apehat.es4j.support.sample.SampleSuperClass;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class CompositeTypeTest {

    private final CompositeType type;
    private final Class<?>[] initClasses;

    public CompositeTypeTest() {
        initClasses = new Class[]{
            SampleClass1.class, SampleClass2.class, SampleMiddleClass1.class
        };
        type = new CompositeType(initClasses);

        type.remove(SampleClass3.class);
    }

    @Test
    public void testAdd() {
        assertFalse(type.isAssignableFrom(SampleSuperClass.class));
        Type newType = type.add(SampleSuperClass.class);
        assertFalse(type.isAssignableFrom(SampleSuperClass.class));
        assertTrue(newType.isAssignableFrom(SampleSuperClass.class));
    }

    @Test
    public void testRemove() {
        assertTrue(type.isAssignableFrom(SampleClass1.class));
        Type newType = type.remove(SampleClass1.class);
        assertTrue(type.isAssignableFrom(SampleClass1.class));
        assertFalse(newType.isAssignableFrom(SampleClass1.class));
    }

    @Test
    public void testIsAssignableFrom() {
        for (Class<?> cls : initClasses) {
            assertTrue(type.isAssignableFrom(cls));
        }
    }
}