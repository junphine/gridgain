/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.ml.math.exceptions;

/**
 * Indicates a cardinality mismatch in matrix or vector operations.
 */
public class CardinalityException extends MathIllegalArgumentException {
    /** */
    private static final long serialVersionUID = 0L;

    /**
     * Creates new cardinality violation exception.
     *
     * @param exp Expected cardinality.
     * @param act Actual cardinality.
     */
    public CardinalityException(int exp, int act) {
        super("Cardinality violation [expected=%d, actual=%d]", exp, act);
    }
}
