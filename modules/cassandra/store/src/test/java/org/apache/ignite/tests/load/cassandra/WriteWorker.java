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

package org.apache.ignite.tests.load.cassandra;

import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.internal.processors.cache.CacheEntryImpl;
import org.apache.ignite.tests.load.Worker;

/**
 * Cassandra direct load tests worker for write operation CacheStore.write
 */
public class WriteWorker extends Worker {
    /** */
    public static final String LOGGER_NAME = "CassandraWriteLoadTest";

    /** */
    public WriteWorker(CacheStore cacheStore, long startPosition, long endPosition) {
        super(cacheStore, startPosition, endPosition);
    }

    /** {@inheritDoc} */
    @Override protected String loggerName() {
        return LOGGER_NAME;
    }

    /** {@inheritDoc} */
    @Override protected boolean batchMode() {
        return false;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override protected void process(CacheStore cacheStore, CacheEntryImpl entry) {
        cacheStore.write(entry);
    }
}
