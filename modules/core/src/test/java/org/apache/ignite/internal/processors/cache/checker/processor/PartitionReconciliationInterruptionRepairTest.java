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

package org.apache.ignite.internal.processors.cache.checker.processor;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.internal.IgniteInterruptedCheckedException;
import org.apache.ignite.internal.processors.cache.GridCacheContext;
import org.apache.ignite.internal.processors.cache.checker.objects.ReconciliationResult;
import org.apache.ignite.internal.processors.cache.checker.objects.RepairRequest;
import org.apache.ignite.internal.processors.cache.verify.RepairAlgorithm;
import org.apache.ignite.internal.visor.checker.VisorPartitionReconciliationTaskArg;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.ThrowUp;
import org.junit.Test;

import static org.apache.ignite.internal.processors.cache.checker.processor.PartitionReconciliationProcessor.TOPOLOGY_CHANGE_MSG;
import static org.apache.ignite.internal.processors.cache.checker.processor.ReconciliationEventListener.WorkLoadStage.FINISHED;

/**
 * Tests different scenario of interruption of repair stage.
 */
public class PartitionReconciliationInterruptionRepairTest extends PartitionReconciliationInterruptionTest {
    /** Zero delay for recheck. */
    private boolean zeroDelay = false;

    /**
     * Stop node during check.
     */
    @Test
    public void testStopNodeDuringCheck() throws Exception {
        interruptionDuringCheck(() -> stopGrid(2), res -> {
            assertFalse(res.partitionReconciliationResult().isEmpty());

            assertErrorMsgLeastOne(res, TOPOLOGY_CHANGE_MSG);
        }, false);
    }

    /**
     * Start node during check.
     */
    @Test
    public void testStartNewNodeDuringCheck() throws Exception {
        interruptionDuringCheck(() -> startGrid(5), res -> {
            assertFalse(res.partitionReconciliationResult().isEmpty());

            assertErrorMsgLeastOne(res, TOPOLOGY_CHANGE_MSG);
        }, false);
    }

    /**
     * Start new client node during check.
     */
    @Test
    public void testStartNewClientNodeDuringCheck() throws Exception {
        batchSize = BROKEN_KEYS_CNT / 3;
        zeroDelay = true;

        interruptionDuringCheck(() -> startClientGrid(5), res -> {
            assertFalse(res.partitionReconciliationResult().isEmpty());

            assertTrue(res.errors().isEmpty());
        }, false);
    }

    /**
     * Stop client node during check.
     */
    @Test
    public void testStopClientNodeDuringCheck() throws Exception {
        batchSize = BROKEN_KEYS_CNT / 3;
        zeroDelay = true;

        startClientGrid(5);

        interruptionDuringCheck(() -> stopGrid(5), res -> {
            assertFalse(res.partitionReconciliationResult().isEmpty());

            assertTrue(res.errors().isEmpty());
        }, false);
    }

    /**
     * Start new thin client node during check.
     */
    @Test
    public void testStartNewThinClientNodeDuringCheck() throws Exception {
        batchSize = BROKEN_KEYS_CNT / 3;
        zeroDelay = true;

        interruptionDuringCheck(() -> Ignition.startClient(new ClientConfiguration().setAddresses("127.0.0.1:10800")), res -> {
            assertFalse(res.partitionReconciliationResult().isEmpty());

            assertTrue(res.errors().isEmpty());
        }, false);
    }

    /**
     * Stop thin client node during check.
     */
    @Test
    public void testStopThinClientNodeDuringCheck() throws Exception {
        batchSize = BROKEN_KEYS_CNT / 3;
        zeroDelay = true;

        IgniteClient client = Ignition.startClient(new ClientConfiguration().setAddresses("127.0.0.1:10800"));

        interruptionDuringCheck(client::close, res -> {
            assertFalse(res.partitionReconciliationResult().isEmpty());

            assertTrue(res.errors().isEmpty());
        }, false);
    }

    /**
     * Create cache during check.
     */
    @Test
    public void testCreateCacheDuringCheck() throws Exception {
        interruptionDuringCheck(() -> client.createCache("SOME_CACHE"), res -> {
            assertFalse(res.partitionReconciliationResult().isEmpty());

            assertErrorMsgLeastOne(res, TOPOLOGY_CHANGE_MSG);
        }, false);
    }

    /**
     * Remove not processed cache during check.
     */
    @Test
    public void testRemoveNotProcessedCacheDuringCheck() throws Exception {
        IgniteCache<Object, Object> notProcessedCache = client.createCache("SOME_CACHE");
        interruptionDuringCheck(notProcessedCache::destroy, res -> {
            assertFalse(res.partitionReconciliationResult().isEmpty());

            assertErrorMsgLeastOne(res, TOPOLOGY_CHANGE_MSG);
        }, false);
    }

    /**
     * Remove processed cache during check.
     */
    @Test
    public void testRemoveProcessedCacheDuringCheck() throws Exception {
        interruptionDuringCheck(() -> client.cache(DEFAULT_CACHE_NAME).destroy(), res -> {
            assertFalse(res.partitionReconciliationResult().isEmpty());

            assertFalse(res.errors().isEmpty());
        }, true);
    }

    /**
     *
     */
    private <E extends Throwable> void interruptionDuringCheck(ThrowUp<E> act,
        Consumer<ReconciliationResult> assertions,
        boolean waitOnProcessing) throws E, InterruptedException, IgniteInterruptedCheckedException {
        CountDownLatch firstRecheckFinished = new CountDownLatch(1);
        CountDownLatch waitInTask = new CountDownLatch(1);
        CountDownLatch waitOnProcessingBeforeAction = new CountDownLatch(1);

        ReconciliationEventListenerProvider.defaultListenerInstance((stage, workload) -> {
            if (firstRecheckFinished.getCount() == 0) {
                try {
                    waitInTask.await();

                    if (waitOnProcessing)
                        waitOnProcessingBeforeAction.await();
                    else
                        Thread.sleep(1_000);
                }
                catch (InterruptedException ignore) {
                }
            }

            if (stage.equals(FINISHED) && workload instanceof RepairRequest)
                firstRecheckFinished.countDown();
        });

        GridCacheContext[] nodeCacheCtxs = new GridCacheContext[NODES_CNT];

        for (int i = 0; i < NODES_CNT; i++)
            nodeCacheCtxs[i] = grid(i).cachex(DEFAULT_CACHE_NAME).context();

        for (int i = 0; i < BROKEN_KEYS_CNT; i++) {
            client.cache(DEFAULT_CACHE_NAME).put(i, i);

            simulateOutdatedVersionCorruption(nodeCacheCtxs[i % NODES_CNT], i);
        }

        VisorPartitionReconciliationTaskArg.Builder builder = new VisorPartitionReconciliationTaskArg.Builder();
        ;
        builder.batchSize(batchSize);
        builder.parallelism(1);
        builder.repair(true);
        builder.repairAlg(RepairAlgorithm.PRIMARY);
        builder.caches(Collections.singleton(DEFAULT_CACHE_NAME));
        builder.locOutput(true);
        builder.recheckAttempts(0);
        if (zeroDelay)
            builder.recheckDelay(0);

        final AtomicReference<ReconciliationResult> res = new AtomicReference<>();

        GridTestUtils.runMultiThreadedAsync(() -> {
            try {
                res.set(partitionReconciliation(client, builder));
            }
            catch (Exception e) {
                log.error("Test failed.", e);
            }
        }, 1, "partitionReconciliation");

        firstRecheckFinished.await();

        waitInTask.countDown();

        act.run();

        waitOnProcessingBeforeAction.countDown();

        assertTrue(GridTestUtils.waitForCondition(() -> res.get() != null, 100_000));

        assertions.accept(res.get());
    }
}
