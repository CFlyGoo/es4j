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

package com.apehat.es4j.util.serializer;

import com.apehat.es4j.NestedCheckException;
import com.apehat.es4j.util.serializer.Deserializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class DefaultDeserializer implements Deserializer<Object> {

    @Override
    public Object deserialize(InputStream inputStream) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(inputStream);
        try {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new NestedCheckException(e);
        }
    }
}