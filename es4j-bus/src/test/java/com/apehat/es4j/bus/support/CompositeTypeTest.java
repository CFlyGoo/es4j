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
import com.apehat.es4j.support.sample.SampleClass4;
import com.apehat.es4j.support.sample.SampleClass5;
import com.apehat.es4j.support.sample.SampleClass6;
import com.apehat.es4j.support.sample.SampleClass7;
import com.apehat.es4j.support.sample.SampleClass8;
import com.apehat.es4j.support.sample.SampleMiddleClass1;
import com.apehat.es4j.support.sample.SampleMiddleClass2;
import com.apehat.es4j.support.sample.SampleMiddleClass3;
import com.apehat.es4j.support.sample.SampleMiddleClass4;
import com.apehat.es4j.support.sample.SampleMiddleInterface1;
import com.apehat.es4j.support.sample.SampleMiddleInterface2;
import com.apehat.es4j.support.sample.SampleMiddleInterface3;
import com.apehat.es4j.support.sample.SampleSuperClass;
import com.apehat.es4j.support.sample.SampleSuperInterface1;
import com.apehat.es4j.support.sample.SampleSuperInterface2;
import java.util.Arrays;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class CompositeTypeTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeTypeTest.class);

    private Type type;
    private Type typeWithRemoved;
    private Class<?>[] initSamples;
    private Class<?>[] removedSamples;

    @SuppressWarnings("StatementWithEmptyBody")
    @BeforeMethod
    public void setUp() {
        // all test sample
        Class<?>[] samples = new Class[]{
            SampleMiddleInterface2.class, SampleMiddleClass1.class, SampleMiddleClass2.class,
            SampleMiddleClass3.class, SampleSuperInterface1.class, SampleSuperInterface2.class,
            SampleClass3.class, SampleClass4.class, SampleClass5.class, SampleClass6.class,
            SampleClass7.class, SampleClass8.class, SampleSuperClass.class,
            SampleMiddleInterface3.class, SampleClass1.class, SampleClass2.class,
            SampleMiddleClass4.class, SampleMiddleInterface1.class
        };

        // init sample
        int initCount;
        while ((initCount = new Random().nextInt(samples.length)) < samples.length * 2 / 3) {
        }
        this.initSamples = new Class[initCount];
        for (int i = 0; i < initCount; i++) {
            initSamples[i] = samples[new Random().nextInt(samples.length)];
        }

        // remove sample
        int removeCount;
        while ((removeCount = new Random().nextInt(initSamples.length / 2)) == 0) {
        }
        this.removedSamples = new Class[removeCount];
        for (int i = 0; i < removeCount; i++) {
            removedSamples[i] = samples[new Random().nextInt(samples.length)];
        }

        // init type
        LOGGER.debug("Construct CompositeType with {}", Arrays.toString(initSamples));
        this.type = new CompositeType(initSamples);
        LOGGER.debug("Remove {} from {}", Arrays.toString(removedSamples), type);
        Type temp = type;
        for (Class<?> removedSample : removedSamples) {
            LOGGER.debug("Will remove singleton type {} from {}", removedSample, temp);
            temp = temp.remove(removedSample);
        }
        typeWithRemoved = temp;
        LOGGER.debug("typeWithRemoved is {}", typeWithRemoved);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructWithNull() {
        new CompositeType();
    }

    @Test(invocationCount = 10)
    public void testAdd() {
        final Class<?> nonAssignableType =
            removedSamples[new Random().nextInt(removedSamples.length)];
        LOGGER.debug("Start test add by {} from {}", nonAssignableType, typeWithRemoved);
        assertFalse(typeWithRemoved.isAssignableFrom(nonAssignableType));
        final Type newType = typeWithRemoved.add(nonAssignableType);
        LOGGER.debug("New type {}", newType);
        assertTrue(newType.isAssignableFrom(nonAssignableType));
        LOGGER.debug("After add {}", typeWithRemoved);
        assertFalse(typeWithRemoved.isAssignableFrom(nonAssignableType));
    }

    @Test(invocationCount = 10)
    public void testRemove() {
        final Class<?> assignableType = initSamples[new Random().nextInt(initSamples.length)];
        LOGGER.debug("Start test remove by {} from {}", assignableType, type);
        assertTrue(type.isAssignableFrom(assignableType));
        final Type newType = type.remove(assignableType);
        LOGGER.debug("New type {}", newType);
        assertFalse(newType.isAssignableFrom(assignableType));
        LOGGER.debug("After remove {}", type);
        assertTrue(type.isAssignableFrom(assignableType));
    }
}