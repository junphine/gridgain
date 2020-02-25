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

package org.apache.ignite.internal.processors.metastorage;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.failure.FailureHandler;
import org.apache.ignite.failure.StopNodeFailureHandler;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.IgniteFutureTimeoutCheckedException;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.IgniteKernal;
import org.apache.ignite.internal.IgnitionEx;
import org.apache.ignite.internal.processors.metastorage.persistence.DistributedMetaStorageImpl;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.SystemPropertiesList;
import org.apache.ignite.testframework.junits.WithSystemProperty;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.apache.ignite.IgniteSystemProperties.IGNITE_GLOBAL_METASTORAGE_HISTORY_MAX_BYTES;
import static org.apache.ignite.internal.SupportFeaturesUtils.IGNITE_BASELINE_AUTO_ADJUST_FEATURE;
import static org.apache.ignite.internal.SupportFeaturesUtils.IGNITE_BASELINE_FOR_IN_MEMORY_CACHES_FEATURE;

/**
 * Test for {@link DistributedMetaStorageImpl} with disabled persistence.
 */
public class DistributedMetaStorageTest extends GridCommonAbstractTest {
    /**
     * Used in tests for updatesCount counter of metastorage and corresponds to keys CLUSTER_ID, CLUSTER_TAG and other
     * initial objects that were added but should not be counted along with keys defined in tests.
     */
    private static final int INITIAL_UPDATES_COUNT = 3;

    /** String exceeding max length of metastorage key. */
    private static final String LONG_KEY;

    /** **/
    private TcpDiscoverySpi customTcpDiscoverySpi = null;

    static {
        String template = "012345678901234567890123456789";

        // Two templates - 60 bytes. Key is considered as long if it is shorter than 62 bytes.
        LONG_KEY = template + template + "01";
    }

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String igniteInstanceName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(igniteInstanceName);

        cfg.setConsistentId(igniteInstanceName);

        cfg.setDataStorageConfiguration(new DataStorageConfiguration()
            .setDefaultDataRegionConfiguration(new DataRegionConfiguration()
                .setPersistenceEnabled(isPersistent())
            )
            .setWalSegments(3)
            .setWalSegmentSize(512 * 1024)
        );

        DiscoverySpi discoSpi = cfg.getDiscoverySpi();

        if (discoSpi instanceof TcpDiscoverySpi) {
            if (customTcpDiscoverySpi != null)
                cfg.setDiscoverySpi(
                    customTcpDiscoverySpi
                        .setIpFinder(((TcpDiscoverySpi)cfg.getDiscoverySpi()).getIpFinder())
                );

            ((TcpDiscoverySpi)discoSpi).setNetworkTimeout(1000);
        }

        if (igniteInstanceName.contains("client"))
            cfg.setClientMode(true);

        return cfg;
    }

    /**
     * @return {@code true} for tests with persistent cluster, {@code false} otherwise.
     */
    protected boolean isPersistent() {
        return false;
    }

    /** {@inheritDoc} */
    @Override protected FailureHandler getFailureHandler(String igniteInstanceName) {
        return new StopNodeFailureHandler();
    }

    /** */
    @Before
    public void before() throws Exception {
        stopAllGrids();
    }

    /** */
    @After
    public void after() throws Exception {
        stopAllGrids();
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testSingleNode() throws Exception {
        IgniteEx ignite = startGrid(0);

        ignite.cluster().active(true);

        DistributedMetaStorage metastorage = ignite.context().distributedMetastorage();

        assertNull(metastorage.read("key"));

        metastorage.write("key", "value");

        assertEquals("value", metastorage.read("key"));

        metastorage.remove("key");

        assertNull(metastorage.read("key"));
    }

    /**
     * Test verifies that Distributed Metastorage on client is not operational until client connects to some cluster.
     *
     * After successful join DMS on client becomes operational.
     *
     * @throws Exception If failed.
     */
    @Test
    @Ignore("https://ggsystems.atlassian.net/browse/GG-27178")
    public void testDistributedMetastorageOperationsOnClient() throws Exception {
        String clientName = "client0";

        String key = "key";
        String value = "value";

        IgniteInternalFuture<IgniteEx> clFut = GridTestUtils.runAsync(() -> startGrid(clientName));

        GridTestUtils.waitForCondition(() -> {
            try {
                IgniteKernal clientGrid = IgnitionEx.gridx(clientName);

                return clientGrid != null && clientGrid.context().distributedMetastorage() != null;
            }
            catch (Exception ignored) {
                return false;
            }
        }, 20_000);

        IgniteKernal cl0 = IgnitionEx.gridx("client0");

        final DistributedMetaStorage clDms = cl0.context().distributedMetastorage();

        assertNotNull(clDms);

        // DMS on client blocks if client is not connected to the cluster
        IgniteInternalFuture fut = GridTestUtils.runAsync(() -> {
            try {
                clDms.write(key, value);
            }
            catch (IgniteCheckedException ignored) {
                // No-op.
            }
        });

        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override public Object call() throws Exception {
                fut.get(1000);

                return null;
            }
        }, IgniteFutureTimeoutCheckedException.class, null);

        startGrid(0);

        clFut.get();

        DistributedMetaStorage clDms0 = cl0.context().distributedMetastorage();

        GridTestUtils.waitForCondition(() -> {
            try {
                return clDms0.read(key) != null;
            }
            catch (IgniteCheckedException ignored) {
                return false;
            }
        }, 20_000);

        assertEquals(value, clDms0.read(key));
    }

    /**
     * Verifies that DistributedMetastorage doesn't allow writes of too long keys (exceeding 64 bytes limit).
     *
     * @throws Exception If failed.
     */
    @Test
    public void testLongKeyOnWrite() throws Exception {
        IgniteEx ignite = startGrid(0);

        ignite.cluster().active(true);

        DistributedMetaStorage metastorage = ignite.context().distributedMetastorage();

        GridTestUtils.assertThrowsAnyCause(null,
            new Callable<Object>() {
                @Override public Object call() throws Exception {
                    metastorage.write(LONG_KEY, "randomValue");

                    return null;
                }
            },
            IgniteCheckedException.class,
            "Key is too long."
        );

        GridTestUtils.assertThrowsAnyCause(null,
            new Callable<Object>() {
                @Override public Object call() throws Exception {
                    metastorage.writeAsync(LONG_KEY, "randomValue");

                    return null;
                }
            },
            IgniteCheckedException.class,
            "Key is too long."
        );
    }

    /**
     * Verifies that DistributedMetastorage doesn't allow writes of too long keys (exceeding 64 bytes limit).
     *
     * @throws Exception If failed.
     */
    @Test
    public void testLongKeyOnCas() throws Exception {
        IgniteEx ignite = startGrid(0);

        ignite.cluster().active(true);

        DistributedMetaStorage metastorage = ignite.context().distributedMetastorage();

        GridTestUtils.assertThrowsAnyCause(null,
            new Callable<Object>() {
                @Override public Object call() throws Exception {
                    metastorage.compareAndSet(LONG_KEY, "randomValue", "newRandomValue");

                    return null;
                }
            },
            IgniteCheckedException.class,
            "Key is too long."
        );

        GridTestUtils.assertThrowsAnyCause(null,
            new Callable<Object>() {
                @Override public Object call() throws Exception {
                    metastorage.compareAndSetAsync(LONG_KEY, "randomValue", "newRandomValue");

                    return null;
                }
            },
            IgniteCheckedException.class,
            "Key is too long."
        );

        GridTestUtils.assertThrowsAnyCause(null,
            new Callable<Object>() {
                @Override public Object call() throws Exception {
                    metastorage.compareAndRemove(LONG_KEY, "randomValue");

                    return null;
                }
            },
            IgniteCheckedException.class,
            "Key is too long."
        );
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testMultipleNodes() throws Exception {
        int cnt = 4;

        startGrids(cnt);

        grid(0).cluster().active(true);

        for (int i = 0; i < cnt; i++) {
            String key = UUID.randomUUID().toString();

            String val = UUID.randomUUID().toString();

            metastorage(i).write(key, val);

            for (int j = 0; j < cnt; j++)
                assertEquals(i + " " + j, val, metastorage(j).read(key));
        }

        for (int i = 1; i < cnt; i++)
            assertDistributedMetastoragesAreEqual(grid(0), grid(i));
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testListenersOnWrite() throws Exception {
        int cnt = 4;

        startGrids(cnt);

        grid(0).cluster().active(true);

        AtomicInteger predCntr = new AtomicInteger();

        for (int i = 0; i < cnt; i++) {
            DistributedMetaStorage metastorage = metastorage(i);

            metastorage.listen(key -> key.startsWith("k"), (key, oldVal, newVal) -> {
                assertNull(oldVal);

                assertEquals("value", newVal);

                predCntr.incrementAndGet();
            });
        }

        metastorage(0).write("key", "value");

        assertEquals(cnt, predCntr.get());

        for (int i = 1; i < cnt; i++)
            assertDistributedMetastoragesAreEqual(grid(0), grid(i));
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testListenersOnRemove() throws Exception {
        int cnt = 4;

        startGrids(cnt);

        grid(0).cluster().active(true);

        metastorage(0).write("key", "value");

        AtomicInteger predCntr = new AtomicInteger();

        for (int i = 0; i < cnt; i++) {
            DistributedMetaStorage metastorage = metastorage(i);

            metastorage.listen(key -> key.startsWith("k"), (key, oldVal, newVal) -> {
                assertEquals("value", oldVal);

                assertNull(newVal);

                predCntr.incrementAndGet();
            });
        }

        metastorage(0).remove("key");

        assertEquals(cnt, predCntr.get());

        for (int i = 1; i < cnt; i++)
            assertDistributedMetastoragesAreEqual(grid(0), grid(i));
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testCas() throws Exception {
        startGrids(2);

        grid(0).cluster().active(true);

        assertFalse(metastorage(0).compareAndSet("key", "expVal", "newVal"));

        assertNull(metastorage(0).read("key"));

        assertFalse(metastorage(0).compareAndRemove("key", "expVal"));

        assertTrue(metastorage(0).compareAndSet("key", null, "val1"));

        assertEquals("val1", metastorage(0).read("key"));

        assertFalse(metastorage(0).compareAndSet("key", null, "val2"));

        assertEquals("val1", metastorage(0).read("key"));

        assertTrue(metastorage(0).compareAndSet("key", "val1", "val3"));

        assertEquals("val3", metastorage(0).read("key"));

        assertFalse(metastorage(0).compareAndRemove("key", "val1"));

        assertEquals("val3", metastorage(0).read("key"));

        assertTrue(metastorage(0).compareAndRemove("key", "val3"));

        assertNull(metastorage(0).read("key"));

        assertDistributedMetastoragesAreEqual(grid(0), grid(1));
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testJoinCleanNode() throws Exception {
        IgniteEx ignite = startGrid(0);

        ignite.cluster().active(true);

        ignite.context().distributedMetastorage().write("key", "value");

        IgniteEx newNode = startGrid(1);

        assertEquals("value", newNode.context().distributedMetastorage().read("key"));

        assertDistributedMetastoragesAreEqual(ignite, newNode);
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    @WithSystemProperty(key = IGNITE_GLOBAL_METASTORAGE_HISTORY_MAX_BYTES, value = "0")
    public void testJoinCleanNodeFullData() throws Exception {
        IgniteEx ignite = startGrid(0);

        ignite.cluster().active(true);

        ignite.context().distributedMetastorage().write("key1", "value1");

        ignite.context().distributedMetastorage().write("key2", "value2");

        startGrid(1);

        assertEquals("value1", metastorage(1).read("key1"));

        assertEquals("value2", metastorage(1).read("key2"));

        assertDistributedMetastoragesAreEqual(ignite, grid(1));
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    @WithSystemProperty(key = IGNITE_GLOBAL_METASTORAGE_HISTORY_MAX_BYTES, value = "0")
    public void testDeactivateActivate() throws Exception {
        startGrid(0);

        grid(0).cluster().active(true);

        metastorage(0).write("key1", "value1");

        metastorage(0).write("key2", "value2");

        grid(0).cluster().active(false);

        startGrid(1);

        grid(0).cluster().active(true);

        assertEquals("value1", metastorage(0).read("key1"));

        assertEquals("value2", metastorage(0).read("key2"));

        assertDistributedMetastoragesAreEqual(grid(0), grid(1));
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testOptimizedWriteTwice() throws Exception {
        IgniteEx igniteEx = startGrid(0);

        igniteEx.cluster().active(true);

        metastorage(0).write("key1", "value1");

        assertEquals(1, metastorage(0).getUpdatesCount() - INITIAL_UPDATES_COUNT);

        metastorage(0).write("key2", "value2");

        assertEquals(2, metastorage(0).getUpdatesCount() - INITIAL_UPDATES_COUNT);

        metastorage(0).write("key1", "value1");

        assertEquals(2, metastorage(0).getUpdatesCount() - INITIAL_UPDATES_COUNT);
    }

    /** */
    @Test
    public void testClient() throws Exception {
        IgniteEx igniteEx = startGrid(0);

        igniteEx.cluster().active(true);

        metastorage(0).write("key0", "value0");

        startClient(1);

        AtomicInteger clientLsnrUpdatesCnt = new AtomicInteger();

        assertEquals(1, metastorage(1).getUpdatesCount() - INITIAL_UPDATES_COUNT);

        assertEquals("value0", metastorage(1).read("key0"));

        metastorage(1).listen(key -> true, (key, oldVal, newVal) -> clientLsnrUpdatesCnt.incrementAndGet());

        metastorage(1).write("key1", "value1");

        assertEquals(1, clientLsnrUpdatesCnt.get());

        assertEquals("value1", metastorage(1).read("key1"));

        assertEquals("value1", metastorage(0).read("key1"));
    }

    /** */
    @Test
    public void testClientReconnect() throws Exception {
        IgniteEx igniteEx = startGrid(0);

        igniteEx.cluster().active(true);

        startClient(1);

        metastorage(0).write("key0", "value0");

        startGrid(2);

        stopGrid(0);

        stopGrid(2);

        startGrid(2).cluster().active(true);

        metastorage(2).write("key1", "value1");

        metastorage(2).write("key2", "value2");

        int expUpdatesCnt = isPersistent() ? 3 : 2;

        // Wait enough to cover failover timeout.
        assertTrue(GridTestUtils.waitForCondition(
            () -> metastorage(1).getUpdatesCount() - INITIAL_UPDATES_COUNT == expUpdatesCnt, 15_000));

        if (isPersistent())
            assertEquals("value0", metastorage(1).read("key0"));

        assertEquals("value1", metastorage(1).read("key1"));

        assertEquals("value2", metastorage(1).read("key2"));
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    @SystemPropertiesList({
        @WithSystemProperty(key = IGNITE_BASELINE_AUTO_ADJUST_FEATURE, value = "true"),
        @WithSystemProperty(key = IGNITE_BASELINE_FOR_IN_MEMORY_CACHES_FEATURE, value = "true"),
    })
    public void testUnstableTopology() throws Exception {
        int cnt = 8;

        startGridsMultiThreaded(cnt);

        grid(0).cluster().active(true);

        stopGrid(0);

        startGrid(0);

        AtomicInteger gridIdxCntr = new AtomicInteger(0);

        AtomicBoolean stop = new AtomicBoolean();

        IgniteInternalFuture<?> fut = multithreadedAsync(() -> {
            int gridIdx = gridIdxCntr.incrementAndGet();

            try {
                while (!stop.get()) {
                    stopGrid(gridIdx, true);

                    Thread.sleep(50L);

                    startGrid(gridIdx);

                    Thread.sleep(50L);
                }
            }
            catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }, cnt - 1);

        long start = System.currentTimeMillis();

        long duration = GridTestUtils.SF.applyLB(15_000, 5_000);

        try {
            while (System.currentTimeMillis() < start + duration) {
                ThreadLocalRandom rnd = ThreadLocalRandom.current();

                metastorage(0).write(
                    "key" + rnd.nextInt(5000), Integer.toString(rnd.nextInt(1000))
                );
            }
        }
        finally {
            stop.set(true);

            fut.get();
        }

        awaitPartitionMapExchange();

        for (int i = 1; i < cnt; i++)
            assertDistributedMetastoragesAreEqual(grid(0), grid(i));
    }

    /** */
    protected IgniteEx startClient(int idx) throws Exception {
        return startGrid(getConfiguration(getTestIgniteInstanceName(idx)).setClientMode(true));
    }

    /**
     * @return {@link DistributedMetaStorage} instance for i'th node.
     */
    protected DistributedMetaStorage metastorage(int i) {
        return grid(i).context().distributedMetastorage();
    }

    /**
     * Assert that two nodes have the same internal state in {@link DistributedMetaStorage}.
     */
    protected void assertDistributedMetastoragesAreEqual(IgniteEx ignite1, IgniteEx ignite2) throws Exception {
        DistributedMetaStorage distributedMetastorage1 = ignite1.context().distributedMetastorage();

        DistributedMetaStorage distributedMetastorage2 = ignite2.context().distributedMetastorage();

        Object ver1 = U.field(distributedMetastorage1, "ver");

        Object ver2 = U.field(distributedMetastorage2, "ver");

        assertEquals(ver1, ver2);

        Object histCache1 = U.field(distributedMetastorage1, "histCache");

        Object histCache2 = U.field(distributedMetastorage2, "histCache");

        assertEquals(histCache1, histCache2);

        Method fullDataMtd = U.findNonPublicMethod(DistributedMetaStorageImpl.class, "localFullData");

        Object[] fullData1 = (Object[])fullDataMtd.invoke(distributedMetastorage1);

        Object[] fullData2 = (Object[])fullDataMtd.invoke(distributedMetastorage2);

        assertEqualsCollections(Arrays.asList(fullData1), Arrays.asList(fullData2));

        // Also check that arrays are sorted.
        Arrays.sort(fullData1, Comparator.comparing(o -> U.field(o, "key")));

        assertEqualsCollections(Arrays.asList(fullData1), Arrays.asList(fullData2));
    }
}
