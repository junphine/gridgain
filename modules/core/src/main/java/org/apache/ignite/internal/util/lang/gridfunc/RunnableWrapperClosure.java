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

package org.apache.ignite.internal.util.lang.gridfunc;

import org.apache.ignite.internal.util.lang.GridAbsClosure;
import org.apache.ignite.internal.util.typedef.internal.S;

/**
 * Closure that wraps given runnable.
 * Note that wrapping closure always returns {@code null}.
 */
public class RunnableWrapperClosure extends GridAbsClosure {
    /** */
    private static final long serialVersionUID = 0L;

    /** */
    private final Runnable r;

    /**
     * @param r Runnable to convert to closure. If {@code null} - no-op closure is returned.
     */
    public RunnableWrapperClosure(Runnable r) {
        this.r = r;
    }

    /** {@inheritDoc} */
    @Override public void apply() {
        if (r != null)
            r.run();
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(RunnableWrapperClosure.class, this);
    }
}
