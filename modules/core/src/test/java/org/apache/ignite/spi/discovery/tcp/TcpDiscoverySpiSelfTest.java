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

package org.apache.ignite.spi.discovery.tcp;

import org.apache.ignite.spi.discovery.AbstractDiscoverySelfTest;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.testframework.junits.spi.GridSpiTest;

/**
 * TCP discovery spi test.
 */
@GridSpiTest(spi = TcpDiscoverySpi.class, group = "Discovery SPI")
public class TcpDiscoverySpiSelfTest extends AbstractDiscoverySelfTest<TcpDiscoverySpi> {
    /** */
    private TcpDiscoveryIpFinder ipFinder =  new TcpDiscoveryVmIpFinder(true);

    /** {@inheritDoc} */
    @Override protected DiscoverySpi getSpi(int idx) {
        TcpDiscoverySpi spi = new TcpDiscoverySpi();

        spi.setMetricsProvider(createMetricsProvider());
        spi.setIpFinder(ipFinder);

        return spi;
    }
}