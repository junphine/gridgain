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

package org.apache.ignite.ml.tree;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Predicate used to define objects that placed in decision tree node.
 */
public interface TreeFilter extends Predicate<double[]>, Serializable {
    /**
     * Returns a composed predicate.
     *
     * @param other Predicate that will be logically-ANDed with this predicate.
     * @return Returns a composed predicate
     */
    public default TreeFilter and(TreeFilter other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
    }
}
