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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.util.typedef.CA;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;

/**
 * Job tries to get cache during topology change.
 */
public class CacheGetFromJobTest extends GridCacheAbstractSelfTest {
    /** {@inheritDoc} */
    @Override protected int gridCount() {
        return 1;
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        stopAllGrids();
    }

    /** {@inheritDoc} */
    @Override protected long getTestTimeout() {
        return 5 * 60_000;
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testTopologyChange() throws Exception {
        final AtomicReference<Exception> err = new AtomicReference<>();

        final AtomicInteger id = new AtomicInteger(1);

        IgniteInternalFuture<?> fut = GridTestUtils.runMultiThreadedAsync(new CA() {
            @Override public void apply() {
                info("Run topology change.");

                try {
                    for (int i = 0; i < 5; i++) {
                        info("Topology change: " + i);

                        startGrid(id.getAndIncrement());
                    }
                }
                catch (Exception e) {
                    err.set(e);

                    log.error("Unexpected exception in topology-change-thread: " + e, e);
                }
            }
        }, 3, "topology-change-thread");

        int cntr = 0;

        while (!fut.isDone()) {
            grid(0).compute().broadcast(new TestJob());

            cntr++;
        }

        log.info("Job execution count: " + cntr);

        Exception err0 = err.get();

        if (err0 != null)
            throw err0;
    }

    /**
     * Test job.
     */
    private static class TestJob implements IgniteCallable<Object> {
        /** Ignite. */
        @IgniteInstanceResource
        private Ignite ignite;

        /** */
        public TestJob() {
            // No-op.
        }

        /** {@inheritDoc} */
        @Override public Object call() throws Exception {
            IgniteCache cache = ignite.cache(DEFAULT_CACHE_NAME);

            assertNotNull(cache);

            assertEquals(0, cache.localSize());

            return null;
        }
    }
}
