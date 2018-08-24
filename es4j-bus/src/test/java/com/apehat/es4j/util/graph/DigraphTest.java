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

package com.apehat.es4j.util.graph;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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
import com.apehat.es4j.util.ClassUtils;
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
public class DigraphTest {

    private static final Set<Class<?>> SAMPLE;
    private static final Map<Class<?>, Set<Class<?>>> ADJACENT_FIRST_CLASSES = new HashMap<>();
    private static final Map<Class<?>, Set<Class<?>>> ADJACENT_REACHABLE_CLASSES = new HashMap<>();
    private static final Map<Class<?>, Set<Class<?>>> FIRST_CLASSES = new HashMap<>();
    private static final Map<Class<?>, Set<Class<?>>> REACHABLE_CLASSES = new HashMap<>();
    private static final Indicator<Class<?>> INDICATOR =
        (o1, o2) -> o1.getSuperclass() == o2 || Arrays.asList(o1.getInterfaces()).contains(o2);

    static {
        Class<?>[] sampleArray = new Class[]{
            SampleMiddleInterface2.class, SampleMiddleClass1.class, SampleMiddleClass2.class,
            SampleMiddleClass3.class, SampleSuperInterface1.class, SampleSuperInterface2.class,
            SampleClass3.class, SampleClass4.class, SampleClass5.class, SampleClass6.class,
            SampleClass7.class, SampleClass8.class, SampleSuperClass.class,
            SampleMiddleInterface3.class, SampleClass1.class, SampleClass2.class,
            SampleMiddleClass4.class, SampleMiddleInterface1.class
        };
        SAMPLE = new HashSet<>(Arrays.asList(sampleArray));
        init();
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Digraph<Class<?>> digraph;

    DigraphTest(Digraph<Class<?>> digraph) {
        assert digraph != null;
        this.digraph = digraph;
    }

    private static void init() {
        for (Class<?> cls : SAMPLE) {
            Set<Class<?>> fcs = new HashSet<>();
            Set<Class<?>> rcs = new HashSet<>();
            Set<Class<?>> afcs = new HashSet<>();
            Set<Class<?>> arcs = new HashSet<>();
            fcs.add(cls);
            rcs.add(cls);
            afcs.add(cls);
            arcs.add(cls);
            FIRST_CLASSES.put(cls, fcs);
            REACHABLE_CLASSES.put(cls, rcs);
            ADJACENT_FIRST_CLASSES.put(cls, afcs);
            ADJACENT_REACHABLE_CLASSES.put(cls, arcs);
        }

        for (Class<?> cls : SAMPLE) {
            Set<Class<?>> supers = ClassUtils.getSuperclassAndInterfaces(cls);
            for (Class<?> aSuper : supers) {
                if (SAMPLE.contains(aSuper)) {
                    ADJACENT_REACHABLE_CLASSES.get(cls).add(aSuper);
                    ADJACENT_FIRST_CLASSES.get(aSuper).add(cls);
                }
            }
        }

        for (Class<?> cls : SAMPLE) {
            Set<Class<?>> supers = ClassUtils.getAllSuperclassesAndInterfaces(cls);
            for (Class<?> aSuper : supers) {
                if (SAMPLE.contains(aSuper)) {
                    REACHABLE_CLASSES.get(cls).add(aSuper);
                    FIRST_CLASSES.get(aSuper).add(cls);
                }
            }
        }
    }

    public static Set<Class<?>> getSample() {
        return SAMPLE;
    }

    static Indicator<Class<?>> getIndicator() {
        return INDICATOR;
    }

    @Test
    public void testGetAdjacentFirstVertices() {
        for (Class<?> cls : getSample()) {
            logger.debug("Start get adjacent first vertices of {}", cls.getSimpleName());
            assertEquals(
                digraph.getAdjacentFirstVertices(cls), ADJACENT_FIRST_CLASSES.get(cls));
        }
    }

    @Test
    public void testGetAdjacentReachableVertices() {
        for (Class<?> cls : getSample()) {
            logger.debug("Start get adjacent reachable vertices of {}", cls);
            assertEquals(
                digraph.getAdjacentReachableVertices(cls), ADJACENT_REACHABLE_CLASSES.get(cls));
        }
    }

    @Test
    public void testGetReachableVertices() {
        for (Class<?> cls : getSample()) {
            logger.debug("Start get reachable vertices of {}", cls);
            assertEquals(digraph.getReachableVertices(cls), REACHABLE_CLASSES.get(cls));
        }
    }

    @Test
    public void testGetFirstVertices() {
        for (Class<?> cls : getSample()) {
            logger.debug("Start get first vertices of {}", cls.getSimpleName());
            assertEquals(digraph.getFirstVertices(cls), FIRST_CLASSES.get(cls));
        }
    }

    @Test
    public void testIsReachable() {
        for (Class<?> cls : getSample()) {
            logger.debug("Start test is reachable of {}", cls.getSimpleName());
            Set<Class<?>> tempSample = REACHABLE_CLASSES.get(cls);
            for (Class<?> sample : tempSample) {
                assertTrue(digraph.isReachable(cls, sample));
                if (sample != cls) {
                    assertFalse(digraph.isReachable(sample, cls));
                }
            }
        }
    }

    @Test
    public void testIsAdjacent() {
        for (Class<?> cls : getSample()) {
            logger.debug("Start test is adjacent of {}", cls.getSimpleName());
            Set<Class<?>> tempSample = new HashSet<>(Arrays.asList(cls.getInterfaces()));
            tempSample.add(cls);
            tempSample.add(cls.getSuperclass());
            for (Class<?> sample : tempSample) {
                if (getSample().contains(sample)) {
                    assertTrue(digraph.isAdjacent(cls, sample));
                    assertTrue(digraph.isAdjacent(sample, cls));
                }
            }
        }
    }
}
