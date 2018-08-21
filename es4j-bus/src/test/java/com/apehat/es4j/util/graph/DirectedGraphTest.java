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
public class DirectedGraphTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final Set<Class<?>> SAMPLE;
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

    private static void init() {
        for (Class<?> cls : SAMPLE) {
            HashSet<Class<?>> fcs = new HashSet<>();
            HashSet<Class<?>> rcs = new HashSet<>();
            fcs.add(cls);
            rcs.add(cls);
            FIRST_CLASSES.put(cls, fcs);
            REACHABLE_CLASSES.put(cls, rcs);
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

    protected static Indicator<Class<?>> getIndicator() {
        return INDICATOR;
    }

    private final DirectedGraph<Class<?>> directedGraph;

    DirectedGraphTest(DirectedGraph<Class<?>> directedGraph) {
        assert directedGraph != null;
        this.directedGraph = directedGraph;
    }

    @Test
    public void testGetLayer() {
        assertEquals(2, directedGraph.getLayer(SampleMiddleInterface3.class));
    }

    @Test
    public void testGetReachableSet() {
        for (Class<?> cls : getSample()) {
            logger.debug("Start getIn reachable set of {}", cls);
            assertEquals(directedGraph.getReachableSet(cls), REACHABLE_CLASSES.get(cls));
        }
    }

    @Test
    public void testGetFirstSet() {
        for (Class<?> cls : getSample()) {
            logger.debug("Start first set of {}", cls.getSimpleName());
            assertEquals(directedGraph.getFirstSet(cls), FIRST_CLASSES.get(cls));
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
                    assertTrue(directedGraph.isAdjacent(cls, sample));
                    assertTrue(directedGraph.isAdjacent(sample, cls));
                }
            }
        }
    }

    @Test
    public void testIsReachable() {
        for (Class<?> cls : getSample()) {
            logger.debug("Start test is reachable of {}", cls.getSimpleName());
            Set<Class<?>> tempSample = REACHABLE_CLASSES.get(cls);
            for (Class<?> sample : tempSample) {
                assertTrue(directedGraph.isReachable(cls, sample));
                if (sample != cls) {
                    assertFalse(directedGraph.isReachable(sample, cls));
                }
            }
        }
    }

    @Test
    public void testGetIn() {
        Class<?>[] array = new Class[]{
            SampleMiddleClass1.class, SampleMiddleClass2.class, SampleMiddleClass3.class,
            SampleMiddleClass4.class,
            SampleMiddleInterface1.class, SampleMiddleInterface2.class, SampleMiddleInterface3.class
        };
        assertEquals(directedGraph.getIn(2), new HashSet<>(Arrays.asList(array)));
    }

    @Test
    public void testGetLayerCount() {
        assertEquals(directedGraph.getLayerCount(), 3);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetInWithOutOfLayerCount() {
        directedGraph.getIn(4);
    }

    @Test
    public void testGetTop() {
        Class<?>[] topClasses = new Class[]{
            SampleSuperInterface1.class, SampleSuperInterface2.class, SampleSuperClass.class
        };
        assertEquals(directedGraph.getTop(), new HashSet<>(Arrays.asList(topClasses)));
    }
}
