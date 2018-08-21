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

package com.apehat.es4j.bug;

import com.apehat.es4j.bus.support.CompositeType;
import com.apehat.es4j.support.sample.SampleClass4;
import com.apehat.es4j.support.sample.SampleClass6;
import com.apehat.es4j.support.sample.SampleMiddleClass1;
import com.apehat.es4j.support.sample.SampleMiddleClass2;
import com.apehat.es4j.support.sample.SampleMiddleClass4;
import com.apehat.es4j.support.sample.SampleSuperInterface2;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class CompositeTypeThrowsStackOverFlowErrorWhenConstruct {

    // Sample
    // [main] DEBUG com.apehat.es4j.bus.support.CompositeTypeTest -
    // Construct CompositeType with [
    // class com.apehat.es4j.support.sample.SampleMiddleClass2,
    // class com.apehat.es4j.support.sample.SampleMiddleClass2,
    // interface com.apehat.es4j.support.sample.SampleSuperInterface2,
    // class com.apehat.es4j.support.sample.SampleClass4,
    // class com.apehat.es4j.support.sample.SampleMiddleClass1,
    // class com.apehat.es4j.support.sample.SampleMiddleClass4,
    // class com.apehat.es4j.support.sample.SampleMiddleClass1,
    // class com.apehat.es4j.support.sample.SampleClass6
    // ]

    // Error
    // Caused by: java.lang.StackOverflowError
    //	at java.util.HashMap$KeyIterator.<init>(HashMap.java:1464)
    //	at java.util.HashMap$KeySet.iterator(HashMap.java:917)
    //	at java.util.HashSet.iterator(HashSet.java:173)
    //	at com.apehat.es4j.util.graph.AdjacencyDigraph$Vertex.adjacencies(AdjacencyDigraph.java:189)
    //	at com.apehat.es4j.util.graph.AdjacencyDigraph$Vertex.adjacentOutVertices(AdjacencyDigraph.java:184)
    //	at com.apehat.es4j.util.graph.AdjacencyDigraph$Vertex.outVertices(AdjacencyDigraph.java:209)
    //	at com.apehat.es4j.util.graph.AdjacencyDigraph$Vertex.outVertices(AdjacencyDigraph.java:213)
    //	at com.apehat.es4j.util.graph.AdjacencyDigraph$Vertex.outVertices(AdjacencyDigraph.java:213)
    //	at com.apehat.es4j.util.graph.AdjacencyDigraph$Vertex.outVertices(AdjacencyDigraph.java:213)

    // Cause:
    // Surface
    // 1. CompositeType hadn't remove duplicates from arguments
    // 2. CompositeType hadn't override equals() and hashCode()
    // Deep
    // Digraph don't support range

    private Class<?>[] sample = new Class[]{
        SampleMiddleClass2.class, SampleMiddleClass2.class,
        SampleSuperInterface2.class, SampleClass4.class,
        SampleMiddleClass1.class, SampleMiddleClass4.class,
        SampleMiddleClass1.class, SampleClass6.class
    };

    @Test
    public void testConstruct() {
        new CompositeType(sample);
    }
}
