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

package org.apache.ignite.internal.managers.communication;

/**
 * This enumeration defines different types of communication
 * message processing by the communication manager.
 */
public class GridIoPolicy {
    /** */
    public static final byte UNDEFINED = -1;

    /** Public execution pool. */
    public static final byte PUBLIC_POOL = 0;

    /** P2P execution pool. */
    public static final byte P2P_POOL = 1;

    /** System execution pool. */
    public static final byte SYSTEM_POOL = 2;

    /** Management execution pool. */
    public static final byte MANAGEMENT_POOL = 3;

    /** Affinity fetch pool. */
    public static final byte AFFINITY_POOL = 4;

    /** Utility cache execution pool. */
    public static final byte UTILITY_CACHE_POOL = 5;

    /** Pool for handling distributed index range requests. */
    public static final byte IDX_POOL = 7;

    /** Data streamer execution pool. */
    public static final byte DATA_STREAMER_POOL = 9;

    /** Query execution pool. */
    public static final byte QUERY_POOL = 10;

    /** Pool for service proxy executions. */
    public static final byte SERVICE_POOL = 11;

    /** Schema pool.  */
    public static final byte SCHEMA_POOL = 12;

    /** Rebalance pool.  */
    public static final byte REBALANCE_POOL = 13;

    /**
     * Defines the range of reserved pools that are not available for plugins.
     * @param key The key.
     * @return If the key corresponds to reserved pool range.
     */
    public static boolean isReservedGridIoPolicy(byte key) {
        return key >= 0 && key <= 31;
    }
}
