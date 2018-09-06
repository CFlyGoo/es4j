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
import com.apehat.clone.Clone;
import com.apehat.clone.CloningContext;
import com.apehat.serializer.DefaultDeserializer;
import com.apehat.serializer.DefaultSerializer;
import com.apehat.util.ClassUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class SerializationClone implements Clone {

    @Override
    public <T> Value<T> deepClone(T prototype, CloningContext context) {
        if (!(prototype instanceof Serializable)) {
            return null;
        }
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                new DefaultSerializer().serialize(prototype, baos);
                byte[] bytes = baos.toByteArray();
                try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                    Class<T> cls = ClassUtils.getParameterizedClass(prototype);
                    return new Value<>(cls.cast(new DefaultDeserializer().deserialize(bais)));
                }
            } catch (IOException e) {
                return null;
            }
    }
}
