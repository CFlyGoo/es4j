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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.AssertJUnit.assertNull;

import com.apehat.Value;
import java.io.Serializable;
import java.util.Objects;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class SerializationCloneTest {

    private Clone clone = new SerializationClone();
    // CloningService is null-able
    private CloningService service = null;

    @Test
    public void testDeepCloneWithNull() {
        Value<Object> value = clone.deepClone(null, service);
        assertNotNull(value);
        assertNull(value.get());
    }

    @Test
    public void testDeepClone() {
        SerializableClass cls = new SerializableClass();
        Value<SerializableClass> cloneValue = clone.deepClone(cls, service);
        assertNotNull(cloneValue);
        assertNotSame(cls, cloneValue.get());
        assertEquals(cloneValue.get(), cls);
    }

    @Test
    public void testDeepCloneWithNonSerializable() {
        assertNull(clone.deepClone(new NonSerializable(), service));
    }

    static class SerializableClass implements Serializable {

        private static final long serialVersionUID = -2366823324417510357L;

        private final Class<?> cls = SerializableClass.class;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SerializableClass that = (SerializableClass) o;
            return Objects.equals(cls, that.cls);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cls);
        }
    }

    private static class NonSerializable {}
}