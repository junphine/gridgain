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

package org.apache.ignite.internal.processors.cache;

import org.apache.ignite.internal.GridKernalContext;

/**
 * Context to get value of cache object.
 */
public interface CacheObjectValueContext {
    /**
     * @return Kernal context.
     */
    public GridKernalContext kernalContext();

    /**
     * @return Copy on get flag.
     */
    public boolean copyOnGet();

    /**
     * @return {@code True} if should store unmarshalled value in cache.
     */
    public boolean storeValue();

    /**
     * @return {@code True} if deployment info should be associated with the objects of this cache.
     */
    public boolean addDeploymentInfo();

    /**
     * @return Binary enabled flag.
     */
    public boolean binaryEnabled();
}
