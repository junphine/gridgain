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

package org.apache.ignite.cache.eviction;

import java.io.Serializable;
import javax.cache.Cache;
import org.apache.ignite.configuration.CacheConfiguration;

/**
 * Eviction filter to specify which entries should not be evicted. Not applicable when
 * calling explicit evict via {@link EvictableEntry#evict()}.
 * If {@link #evictAllowed(Cache.Entry)} method returns {@code false} then eviction
 * policy will not be notified and entry will never be evicted.
 * <p>
 * Eviction filter can be configured via {@link CacheConfiguration#getEvictionFilter()}
 * configuration property. Default value is {@code null} which means that all
 * cache entries will be tracked by eviction policy.
 */
public interface EvictionFilter<K, V> extends Serializable {
    /**
     * Checks if entry may be evicted from cache.
     *
     * @param entry Cache entry.
     * @return {@code True} if it is allowed to evict this entry.
     */
    public boolean evictAllowed(Cache.Entry<K, V> entry);
}