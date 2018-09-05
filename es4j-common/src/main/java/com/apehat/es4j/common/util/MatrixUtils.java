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

package com.apehat.es4j.common.util;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class MatrixUtils {

    private MatrixUtils() {
    }

    public static byte[][] mul(byte[][] a, byte[][] b) {
        assert a[0].length == b.length;
        final int rowCount = a.length;
        final int columnCount = b[0].length;
        byte[][] result = new byte[rowCount][columnCount];
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                for (int k = 0; k < b.length; k++) {
                    result[r][c] += a[r][k] * b[k][c];
                }
            }
        }
        return result;
    }
}
