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

package org.apache.ignite.configuration;

import java.util.List;
import java.util.Map;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.spi.communication.CommunicationSpi;

/**
 * Communication Failure Context.
 */
public interface CommunicationFailureContext {
    /**
     * @return Current topology snapshot.
     */
    public List<ClusterNode> topologySnapshot();

    /**
     * @param node1 First node.
     * @param node2 Second node.
     * @return {@code True} if {@link CommunicationSpi} is able to establish connection from first node to second node.
     */
    public boolean connectionAvailable(ClusterNode node1, ClusterNode node2);

    /**
     * @return Currently started caches.
     */
    public Map<String, CacheConfiguration<?, ?>> startedCaches();

    /**
     * @param cacheName Cache name.
     * @return Cache partitions affinity assignment.
     */
    public List<List<ClusterNode>> cacheAffinity(String cacheName);

    /**
     * @param cacheName Cache name.
     * @return Cache partitions owners.
     */
    public List<List<ClusterNode>> cachePartitionOwners(String cacheName);

    /**
     * @param node Node to kill.
     */
    public void killNode(ClusterNode node);
}
