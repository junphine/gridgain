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

package org.apache.ignite.internal.processors.cache;

import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.processors.query.QuerySchema;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for CacheComparators from ClusterCachesInfo
 */
public class CacheComparatorTest {
    /**
     * Test if comparator not violates its general contract
     */
    @Test
    public void testDirect() {
        DynamicCacheDescriptor desc1 = new DynamicCacheDescriptor(null,
            new CacheConfiguration().setName("1111"), CacheType.DATA_STRUCTURES,
            null, true, null, true,
            false, null, new QuerySchema(), null);

        DynamicCacheDescriptor desc2 = new DynamicCacheDescriptor(null,
            new CacheConfiguration().setName("2222"), CacheType.INTERNAL,
            null, true, null, true,
            false, null, new QuerySchema(), null);

        assertEquals(-1,
            ClusterCachesInfo.CacheComparators.DIRECT.compare(desc1, desc2));

        assertEquals(1,
            ClusterCachesInfo.CacheComparators.DIRECT.compare(desc2, desc1));
    }
}
