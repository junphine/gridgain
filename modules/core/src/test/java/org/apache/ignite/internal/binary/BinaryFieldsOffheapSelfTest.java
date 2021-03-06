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

package org.apache.ignite.internal.binary;

import org.apache.ignite.internal.util.GridConcurrentHashSet;
import org.apache.ignite.internal.util.GridUnsafe;

/**
 * Field tests for heap-based binaries.
 */
public class BinaryFieldsOffheapSelfTest extends BinaryFieldsAbstractSelfTest {
    /** Allocated unsafe pointer. */
    private final GridConcurrentHashSet<Long> ptrs = new GridConcurrentHashSet<>();

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        super.afterTest();

        // Cleanup allocated objects.
        for (Long ptr : ptrs)
            GridUnsafe.freeMemory(ptr);

        ptrs.clear();
    }

    /** {@inheritDoc} */
    @Override protected BinaryObjectExImpl toBinary(BinaryMarshaller marsh, Object obj) throws Exception {
        byte[] arr = marsh.marshal(obj);

        long ptr = GridUnsafe.allocateMemory(arr.length);

        ptrs.add(ptr);

        GridUnsafe.copyHeapOffheap(arr, GridUnsafe.BYTE_ARR_OFF, ptr, arr.length);

        return new BinaryObjectOffheapImpl(binaryContext(marsh), ptr, 0, arr.length);
    }
}
