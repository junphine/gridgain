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

package org.apache.ignite.internal;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.plugin.CachePluginConfiguration;
import org.apache.ignite.plugin.CachePluginContext;

/**
 * Cache plugin context.
 */
public class GridCachePluginContext<C extends CachePluginConfiguration> implements CachePluginContext<C> {
    /** */
    private final GridKernalContext ctx;

    /** */
    private final CacheConfiguration igniteCacheCfg;

    /**
     * @param ctx Kernal context.
     * @param igniteCacheCfg Ignite config.
     */
    public GridCachePluginContext(GridKernalContext ctx, CacheConfiguration igniteCacheCfg) {
        this.ctx = ctx;
        this.igniteCacheCfg = igniteCacheCfg;
    }

    @Override public IgniteConfiguration igniteConfiguration() {
        return ctx.config();
    }

    /** {@inheritDoc} */
    @Override public CacheConfiguration igniteCacheConfiguration() {
        return igniteCacheCfg;
    }

    /** {@inheritDoc} */
    @Override public Ignite grid() {        
        return ctx.grid();
    }
    
    /** {@inheritDoc} */
    @Override public ClusterNode localNode() {
        return ctx.discovery().localNode();
    }

    /** {@inheritDoc} */
    @Override public IgniteLogger log(Class<?> cls) {
        return ctx.log(cls);
    }
}