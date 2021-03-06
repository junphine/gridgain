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

package org.apache.ignite.testsuites;

import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccClientReconnectTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccClusterRestartTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccConfigurationValidationTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccIteratorWithConcurrentTransactionTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccLocalEntriesWithConcurrentTransactionTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccOperationChecksTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccPartitionedCoordinatorFailoverTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccProcessorLazyStartTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccProcessorTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccRemoteTxOnNearNodeStartTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccReplicatedCoordinatorFailoverTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccScanQueryWithConcurrentTransactionTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccSizeWithConcurrentTransactionTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccTransactionsTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccTxFailoverTest;
import org.apache.ignite.internal.processors.cache.mvcc.CacheMvccVacuumTest;
import org.apache.ignite.internal.processors.cache.mvcc.MvccCachePeekTest;
import org.apache.ignite.internal.processors.cache.mvcc.MvccUnsupportedTxModesTest;
import org.apache.ignite.internal.processors.datastreamer.DataStreamProcessorMvccPersistenceSelfTest;
import org.apache.ignite.internal.processors.datastreamer.DataStreamProcessorMvccSelfTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/** */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    CacheMvccTransactionsTest.class,
    CacheMvccProcessorTest.class,
    CacheMvccVacuumTest.class,
    CacheMvccConfigurationValidationTest.class,

    DataStreamProcessorMvccSelfTest.class,
    DataStreamProcessorMvccPersistenceSelfTest.class,
    CacheMvccOperationChecksTest.class,

    CacheMvccRemoteTxOnNearNodeStartTest.class,

    MvccUnsupportedTxModesTest.class,

    MvccCachePeekTest.class,

    // Concurrent ops tests.
    CacheMvccIteratorWithConcurrentTransactionTest.class,
    CacheMvccLocalEntriesWithConcurrentTransactionTest.class,
    CacheMvccScanQueryWithConcurrentTransactionTest.class,
    CacheMvccSizeWithConcurrentTransactionTest.class,

    // Failover tests.
    CacheMvccTxFailoverTest.class,
    CacheMvccClusterRestartTest.class,
    CacheMvccPartitionedCoordinatorFailoverTest.class,
    CacheMvccReplicatedCoordinatorFailoverTest.class,
    CacheMvccProcessorLazyStartTest.class,
    CacheMvccClientReconnectTest.class
})
public class IgniteCacheMvccTestSuite {
}
