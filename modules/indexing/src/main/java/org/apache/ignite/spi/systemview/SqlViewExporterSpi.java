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

package org.apache.ignite.spi.systemview;

import java.util.function.Predicate;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.IgnitionEx;
import org.apache.ignite.internal.processors.query.h2.IgniteH2Indexing;
import org.apache.ignite.internal.processors.query.h2.SchemaManager;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.spi.IgniteSpiAdapter;
import org.apache.ignite.spi.IgniteSpiContext;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.systemview.view.SystemView;
import org.jetbrains.annotations.Nullable;

import static org.apache.ignite.internal.processors.query.QueryUtils.SCHEMA_SYS;

/**
 * This SPI implementation exports metrics as SQL views.
 *
 * Note, instance of this class created with reflection.
 * @see IgnitionEx#SYSTEM_VIEW_SQL_SPI
 */
public class SqlViewExporterSpi extends IgniteSpiAdapter implements SystemViewExporterSpi {
    /** System view filter. */
    @Nullable private Predicate<SystemView<?>> filter;

    /** System view registry. */
    private ReadOnlySystemViewRegistry sysViewReg;

    /** Schema manager. */
    private SchemaManager mgr;

    /** {@inheritDoc} */
    @Override protected void onContextInitialized0(IgniteSpiContext spiCtx) throws IgniteSpiException {
        GridKernalContext ctx = ((IgniteEx)ignite()).context();

        this.mgr = ((IgniteH2Indexing)ctx.query().getIndexing()).schemaManager();

        sysViewReg.forEach(this::register);
        sysViewReg.addSystemViewCreationListener(this::register);
    }

    /**
     * Registers system view as SQL View.
     *
     * @param sysView System view.
     */
    private void register(SystemView<?> sysView) {
        if (filter != null && !filter.test(sysView)) {
            if (log.isDebugEnabled())
                U.debug(log, "System view filtered and will not be registered.[name=" + sysView.name() + ']');

            return;
        }
        else if (log.isDebugEnabled())
            log.debug("Found new system view [name=" + sysView.name() + ']');

        GridKernalContext ctx = ((IgniteEx)ignite()).context();

        SystemViewLocal<?> view = new SystemViewLocal<>(ctx, sysView);

        try {
            mgr.createSystemView(SCHEMA_SYS, view);
        }
        catch (IgniteCheckedException e) {
            throw new IgniteSpiException(e);
        }
    }

    /** {@inheritDoc} */
    @Override public void spiStart(@Nullable String igniteInstanceName) throws IgniteSpiException {
        // No-op.
    }

    /** {@inheritDoc} */
    @Override public void spiStop() throws IgniteSpiException {
        // No-op.
    }

    /** {@inheritDoc} */
    @Override public void setSystemViewRegistry(ReadOnlySystemViewRegistry mlreg) {
        this.sysViewReg = mlreg;
    }

    /** {@inheritDoc} */
    @Override public void setExportFilter(Predicate<SystemView<?>> filter) {
        this.filter = filter;
    }
}
