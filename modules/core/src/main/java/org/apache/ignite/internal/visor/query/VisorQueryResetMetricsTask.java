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

package org.apache.ignite.internal.visor.query;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.internal.processors.task.GridInternal;
import org.apache.ignite.internal.processors.task.GridVisorManagementTask;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.visor.VisorJob;
import org.apache.ignite.internal.visor.VisorOneNodeTask;

/**
 * Reset compute grid query metrics.
 */
@GridInternal
@GridVisorManagementTask
public class VisorQueryResetMetricsTask extends VisorOneNodeTask<VisorQueryResetMetricsTaskArg, Void> {
    /** */
    private static final long serialVersionUID = 0L;

    /** {@inheritDoc} */
    @Override protected VisorQueryResetMetricsJob job(VisorQueryResetMetricsTaskArg arg) {
        return new VisorQueryResetMetricsJob(arg, debug);
    }

    /**
     * Job that reset cache query metrics.
     */
    private static class VisorQueryResetMetricsJob extends VisorJob<VisorQueryResetMetricsTaskArg, Void> {
        /** */
        private static final long serialVersionUID = 0L;

        /**
         * @param arg Cache name to reset query metrics for.
         * @param debug Debug flag.
         */
        private VisorQueryResetMetricsJob(VisorQueryResetMetricsTaskArg arg, boolean debug) {
            super(arg, debug);
        }

        /** {@inheritDoc} */
        @Override protected Void run(VisorQueryResetMetricsTaskArg arg) {
            String cacheName = arg.getCacheName();

            IgniteCache cache = ignite.cache(cacheName);

            if (cache == null)
                throw new IllegalStateException("Failed to find cache for name: " + cacheName);

            cache.resetQueryMetrics();

            return null;
        }

        /** {@inheritDoc} */
        @Override public String toString() {
            return S.toString(VisorQueryResetMetricsJob.class, this);
        }
    }
}
