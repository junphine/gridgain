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

package org.apache.ignite.internal.processors.metric.impl;

import java.util.concurrent.atomic.LongAdder;
import java.util.function.LongConsumer;
import org.jetbrains.annotations.Nullable;

/**
 * Long metric implementation based on {@link LongAdder} with {@link #delegate}.
 */
public class LongAdderWithDelegateMetric extends LongAdderMetric {
    /** Delegate. */
    private LongConsumer delegate;

    /**
     * @param name Name.
     * @param delegate Delegate to which all updates from new metric will be delegated to.
     * @param descr Description.
     */
    public LongAdderWithDelegateMetric(String name, LongConsumer delegate, @Nullable String descr) {
        super(name, descr);

        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override public void add(long x) {
        super.add(x);

        delegate.accept(x);
    }
}
