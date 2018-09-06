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

package com.apehat;

import java.io.IOException;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class NestedIOException extends NestedCheckException {

    private static final long serialVersionUID = -2350695449604649925L;

    public NestedIOException(String message, IOException cause) {
        super(message, cause);
    }

    public NestedIOException(IOException cause) {
        super(cause);
    }

    public NestedIOException(String message, IOException cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
