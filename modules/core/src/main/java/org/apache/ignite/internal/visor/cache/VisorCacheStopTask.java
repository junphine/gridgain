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

package org.apache.ignite.internal.visor.cache;

import java.util.Collection;
import java.util.HashSet;
import org.apache.ignite.internal.processors.task.GridInternal;
import org.apache.ignite.internal.processors.task.GridVisorManagementTask;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.visor.VisorJob;
import org.apache.ignite.internal.visor.VisorOneNodeTask;

/**
 * Task that stop specified caches on specified node.
 */
@GridInternal
@GridVisorManagementTask
public class VisorCacheStopTask extends VisorOneNodeTask<VisorCacheStopTaskArg, Void> {
    /** */
    private static final long serialVersionUID = 0L;

    /** {@inheritDoc} */
    @Override protected VisorCacheStopJob job(VisorCacheStopTaskArg arg) {
        return new VisorCacheStopJob(arg, debug);
    }

    /**
     * Job that stop specified caches.
     */
    private static class VisorCacheStopJob extends VisorJob<VisorCacheStopTaskArg, Void> {
        /** */
        private static final long serialVersionUID = 0L;

        /**
         * Create job.
         *
         * @param arg Task argument.
         * @param debug Debug flag.
         */
        private VisorCacheStopJob(VisorCacheStopTaskArg arg, boolean debug) {
            super(arg, debug);
        }

        /** {@inheritDoc} */
        @Override protected Void run(VisorCacheStopTaskArg arg) {
            Collection<String> cacheNames = F.isEmpty(arg.getCacheNames())
                ? F.asList(arg.getCacheName())
                : new HashSet<>(arg.getCacheNames());

            if (F.isEmpty(cacheNames))
                throw new IllegalStateException("Cache names was not specified.");

            ignite.destroyCaches(cacheNames);

            return null;
        }

        /** {@inheritDoc} */
        @Override public String toString() {
            return S.toString(VisorCacheStopJob.class, this);
        }
    }
}
