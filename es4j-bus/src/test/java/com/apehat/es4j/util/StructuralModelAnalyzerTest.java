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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class StructuralModelAnalyzerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StructuralModelAnalyzerTest.class);

    private final StructuralModelAnalyzer<Class<?>> analyzer;
    private final Set<Class<?>> sample;

    private Map<Class<?>, Set<Class<?>>> firstClasses = new HashMap<>();
    private Map<Class<?>, Set<Class<?>>> reachableClasses = new HashMap<>();

    public StructuralModelAnalyzerTest() {
        Class<?>[] sampleArray = new Class[]{
            MiddleInterface2.class, MiddleClass1.class, MiddleClass2.class,
            MiddleClass3.class, SuperInterface1.class, SuperInterface2.class,
            Class3.class, Class4.class, Class5.class, Class6.class, Class7.class,
            Class9.class, Class10.class, Class11.class, Class12.class, Class13.class,
            Class8.class, SuperClass1.class, MiddleInterface3.class, Class1.class,
            Class2.class, Class14.class, Class15.class, Class16.class, MiddleClass4.class,
            MiddleInterface1.class
        };
        this.sample = new HashSet<>(Arrays.asList(sampleArray));
        this.analyzer = new StructuralModelAnalyzer<>(this.sample, (o1, o2) ->
            o1 == o2 || Arrays.asList(o1.getInterfaces()).contains(o2) || o1.getSuperclass() == o2);
        init();
    }

    private void init() {
        for (Class<?> cls : sample) {
            HashSet<Class<?>> fcs = new HashSet<>();
            HashSet<Class<?>> rcs = new HashSet<>();
            fcs.add(cls);
            rcs.add(cls);
            firstClasses.put(cls, fcs);
            reachableClasses.put(cls, rcs);
        }

        for (Class<?> cls : sample) {
            Set<Class<?>> supers = getSupers(cls);
            for (Class<?> aSuper : supers) {
                if (sample.contains(aSuper)) {
                    reachableClasses.get(cls).add(aSuper);
                    firstClasses.get(aSuper).add(cls);
                }
            }
        }
    }

    private Set<Class<?>> getSupers(Class<?> cls) {
        Class<?> superclass = cls.getSuperclass();
        HashSet<Class<?>> supers = new HashSet<>(Arrays.asList(cls.getInterfaces()));
        if (superclass != null) {
            supers.add(superclass);
        }
        HashSet<Class<?>> temp = new HashSet<>();
        for (Class<?> aSuper : supers) {
            temp.addAll(getSupers(aSuper));
        }
        supers.addAll(temp);
        return supers;
    }

    @Test
    public void testGetReachableSet() {
        for (Class<?> cls : sample) {
            LOGGER.debug("Start get reachable set of {}", cls);
            assertEquals(analyzer.getReachableSet(cls), reachableClasses.get(cls));
        }
    }

    @Test
    public void testGetFirstSet() {
        for (Class<?> cls : sample) {
            LOGGER.debug("Start first set of {}", cls.getSimpleName());
            assertEquals(analyzer.getFirstSet(cls), firstClasses.get(cls));
        }
    }

    private interface SuperInterface1 {}

    private interface SuperInterface2 {}

    private interface MiddleInterface1
        extends SuperInterface1 {}

    private interface MiddleInterface2
        extends SuperInterface2 {}

    private interface MiddleInterface3
        extends SuperInterface1,
        SuperInterface2 {}

    private static class SuperClass1 {}

    private static class MiddleClass1
        extends SuperClass1 {}

    private static class MiddleClass2
        extends SuperClass1 implements SuperInterface1 {}

    private static class MiddleClass3
        extends SuperClass1 implements SuperInterface2 {}

    private static class MiddleClass4
        extends SuperClass1 implements SuperInterface1, SuperInterface2 {}

    private static class Class1
        extends MiddleClass1 {}

    private static class Class2
        extends MiddleClass1 implements SuperInterface2 {}

    private static class Class3
        extends MiddleClass1 implements MiddleInterface1 {}

    private static class Class4
        extends MiddleClass1 implements MiddleInterface2 {}

    private static class Class5
        extends MiddleClass1 implements MiddleInterface3 {}

    private static class Class6
        extends MiddleClass1 implements MiddleInterface1, MiddleInterface2 {}

    private static class Class7
        extends MiddleClass1 implements MiddleInterface1, MiddleInterface3 {}

    private static class Class8
        extends MiddleClass1 implements MiddleInterface1, MiddleInterface2, MiddleInterface3 {}

    private static class Class9
        extends MiddleClass1 {}

    private static class Class10
        extends MiddleClass1 implements SuperInterface2 {}

    private static class Class11
        extends MiddleClass1 implements MiddleInterface1 {}

    private static class Class12
        extends MiddleClass1 implements MiddleInterface2 {}

    private static class Class13
        extends MiddleClass1 implements MiddleInterface3 {}

    private static class Class14
        extends MiddleClass1 implements MiddleInterface1, MiddleInterface2 {}

    private static class Class15
        extends MiddleClass1 implements MiddleInterface1, MiddleInterface3 {}

    private static class Class16
        extends MiddleClass1 implements MiddleInterface1, MiddleInterface2, MiddleInterface3 {}
}