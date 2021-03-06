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
package org.apache.ignite.internal.processors.cache.persistence.evict;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.lifecycle.LifecycleAware;

/**
 * Entry point for per-page eviction. Accepts information about touching data pages,
 * capable of evicting "the least needed" page (according to implemented eviction algorithm).
 */
public interface PageEvictionTracker extends LifecycleAware {
    /**
     * Call this method when data page is accessed.
     *
     * @param pageId Page id.
     * @throws IgniteCheckedException In case of page memory error.
     */
    public void touchPage(long pageId) throws IgniteCheckedException;

    /**
     * Evicts one data page.
     * In most cases, all entries will be removed from the page.
     * Method guarantees removing at least one entry from "evicted" data page. Removing all entries may be
     * not possible, as some of them can be used by active transactions.
     *
     * @throws IgniteCheckedException In case of page memory error.
     */
    public void evictDataPage() throws IgniteCheckedException;

    /**
     * Call this method when last entry is removed from data page.
     *
     * @param pageId Page id.
     * @throws IgniteCheckedException In case of page memory error.
     */
    public void forgetPage(long pageId) throws IgniteCheckedException;
}
