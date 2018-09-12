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

import com.apehat.Value;
import com.apehat.serializer.DefaultDeserializer;
import com.apehat.serializer.DefaultSerializer;
import com.apehat.serializer.Deserializer;
import com.apehat.serializer.Serializer;
import com.apehat.util.ClassUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class SerializationClone implements Clone {

    private static final Serializer<Object> DEFAULT_SERIALIZER = new DefaultSerializer();
    private static final Deserializer<Object> DEFAULT_DESERIALIZER = new DefaultDeserializer();

    private final Serializer<Object> serializer;
    private final Deserializer<Object> deserializer;

    public SerializationClone() {
        this(DEFAULT_SERIALIZER, DEFAULT_DESERIALIZER);
    }

    public SerializationClone(Serializer<Object> serializer, Deserializer<Object> deserializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public <T> Value<T> deepClone(T prototype, CloningService service) {
        if (prototype == null) {
            return Value.empty();
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            serializer.serialize(prototype, baos);
            byte[] bytes = baos.toByteArray();
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                return new Value<>(ClassUtils.getParameterizedClass(prototype)
                    .cast(deserializer.deserialize(bais)));
            }
        } catch (Exception e) {
            return null;
        }
    }
}
