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

package org.apache.ignite.internal.processors.cache.query;

import java.util.UUID;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.managers.communication.GridIoPolicy;
import org.apache.ignite.internal.processors.cache.GridCacheContext;
import org.apache.ignite.internal.util.lang.GridPlainRunnable;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.lang.IgniteReducer;
import org.apache.ignite.marshaller.Marshaller;

/**
 * Local query future.
 */
public class GridCacheLocalQueryFuture<K, V, R> extends GridCacheQueryFutureAdapter<K, V, R> {
    /** */
    private static final long serialVersionUID = 0L;

    /** */
    private Runnable run;

    /** */
    private IgniteInternalFuture<?> fut;

    /**
     * @param ctx Context.
     * @param qry Query.
     */
    protected GridCacheLocalQueryFuture(GridCacheContext<K, V> ctx, GridCacheQueryBean qry) {
        super(ctx, qry, true);

        run = new LocalQueryRunnable();
    }

    /**
     * Executes query runnable.
     */
    void execute() {
        fut = cctx.kernalContext().closure().runLocalSafe(run, GridIoPolicy.QUERY_POOL);
    }

    /** {@inheritDoc} */
    @Override protected void cancelQuery() throws IgniteCheckedException {
        if (fut != null)
            fut.cancel();
    }

    /** {@inheritDoc} */
    @Override protected boolean onPage(UUID nodeId, boolean last) {
        return last;
    }

    /** {@inheritDoc} */
    @Override protected void loadPage() {
        // No-op.
    }

    /** {@inheritDoc} */
    @Override protected void loadAllPages() {
        // No-op.
    }

    /** {@inheritDoc} */
    @Override public void awaitFirstPage() throws IgniteCheckedException {
        get();
    }

    /** */
    private class LocalQueryRunnable implements GridPlainRunnable {
        /** {@inheritDoc} */
        @Override public void run() {
            try {
                qry.query().validate();

                if (fields())
                    cctx.queries().runFieldsQuery(localQueryInfo());
                else
                    cctx.queries().runQuery(localQueryInfo());
            }
            catch (Throwable e) {
                onDone(e);

                if (e instanceof Error)
                    throw (Error)e;
            }
        }

        /**
         * @return Query info.
         * @throws IgniteCheckedException In case of error.
         */
        private GridCacheQueryInfo localQueryInfo() throws IgniteCheckedException {
            GridCacheQueryBean qry = query();

            Marshaller marsh = cctx.marshaller();

            IgniteReducer<Object, Object> rdc = qry.reducer() != null ?
                U.<IgniteReducer<Object, Object>>unmarshal(marsh, U.marshal(marsh, qry.reducer()),
                    U.resolveClassLoader(cctx.gridConfig())) : null;

            IgniteClosure<Object, Object> trans = qry.transform() != null ?
                U.<IgniteClosure<Object, Object>>unmarshal(marsh, U.marshal(marsh, qry.transform()),
                    U.resolveClassLoader(cctx.gridConfig())) : null;

            return new GridCacheQueryInfo(
                true,
                trans,
                rdc,
                qry.query(),
                GridCacheLocalQueryFuture.this,
                cctx.localNodeId(),
                cctx.io().nextIoId(),
                qry.query().includeMetadata(),
                true,
                qry.arguments()
            );
        }
    }
}