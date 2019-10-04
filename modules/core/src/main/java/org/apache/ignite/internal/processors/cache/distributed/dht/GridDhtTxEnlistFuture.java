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

package org.apache.ignite.internal.processors.cache.distributed.dht;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.processors.cache.CacheEntryPredicate;
import org.apache.ignite.internal.processors.cache.CacheInvokeResult;
import org.apache.ignite.internal.processors.cache.GridCacheContext;
import org.apache.ignite.internal.processors.cache.GridCacheReturn;
import org.apache.ignite.internal.processors.cache.GridCacheUpdateTxResult;
import org.apache.ignite.internal.processors.cache.KeyCacheObject;
import org.apache.ignite.internal.processors.cache.mvcc.MvccSnapshot;
import org.apache.ignite.internal.processors.cache.version.GridCacheVersion;
import org.apache.ignite.internal.processors.query.EnlistOperation;
import org.apache.ignite.internal.processors.query.UpdateSourceIterator;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.lang.IgniteUuid;
import org.jetbrains.annotations.Nullable;

import static org.apache.ignite.plugin.extensions.communication.ProcessingTimeLoggableResponse.INVALID_TIMESTAMP;

/**
 * Future processing transaction enlisting and locking of entries produces by cache API operations.
 */
public final class GridDhtTxEnlistFuture extends GridDhtTxAbstractEnlistFuture<GridCacheReturn> implements UpdateSourceIterator<Object> {
    /** Enlist operation. */
    private EnlistOperation op;

    /** Source iterator. */
    private Iterator<Object> it;

    /** Future result. */
    private GridCacheReturn res;

    /** Need result flag. If {@code True} previous value should be returned as well. */
    private boolean needRes;

    /** Send timestamp. */
    private long sendTimestamp = INVALID_TIMESTAMP;

    /** Receive timestamp. */
    private long receiveTimestamp = INVALID_TIMESTAMP;

    /**
     * Constructor.
     *
     * @param nearNodeId Near node ID.
     * @param nearLockVer Near lock version.
     * @param mvccSnapshot Mvcc snapshot.
     * @param threadId Thread ID.
     * @param nearFutId Near future id.
     * @param nearMiniId Near mini future id.
     * @param tx Transaction.
     * @param timeout Lock acquisition timeout.
     * @param cctx Cache context.
     * @param rows Collection of rows.
     * @param op Operation.
     * @param filter Filter.
     * @param needRes Return previous value flag.
     * @param keepBinary Keep binary flag.
     */
    public GridDhtTxEnlistFuture(UUID nearNodeId,
        GridCacheVersion nearLockVer,
        MvccSnapshot mvccSnapshot,
        long threadId,
        IgniteUuid nearFutId,
        int nearMiniId,
        GridDhtTxLocalAdapter tx,
        long timeout,
        GridCacheContext<?, ?> cctx,
        Collection<Object> rows,
        EnlistOperation op,
        @Nullable CacheEntryPredicate filter,
        boolean needRes,
        boolean keepBinary)
    {
        super(nearNodeId,
            nearLockVer,
            mvccSnapshot,
            threadId,
            nearFutId,
            nearMiniId,
            tx,
            timeout,
            cctx,
            filter,
            keepBinary);

        this.op = op;
        this.needRes = needRes;

        it = rows.iterator();

        res = new GridCacheReturn(cctx.localNodeId().equals(nearNodeId), false);

        skipNearNodeUpdates = true;
    }

    /** {@inheritDoc} */
    @Override protected UpdateSourceIterator<?> createIterator() throws IgniteCheckedException {
        return this;
    }

    /** {@inheritDoc} */
    @Override @Nullable protected GridCacheReturn result0() {
        return res;
    }

    /** {@inheritDoc} */
    @Override protected void onEntryProcessed(KeyCacheObject key, GridCacheUpdateTxResult txRes) {
        assert txRes.invokeResult() == null || needRes;

        res.success(txRes.success());

        if(txRes.invokeResult() != null) {
            res.invokeResult(true);

            CacheInvokeResult invokeRes = txRes.invokeResult();

            if (invokeRes.result() != null || invokeRes.error() != null)
                res.addEntryProcessResult(cctx, key, null, invokeRes.result(), invokeRes.error(), keepBinary);
        }
        else if (needRes)
            res.set(cctx, txRes.prevValue(), txRes.success(), keepBinary);
    }

    /** {@inheritDoc} */
    public boolean needResult() {
        return needRes;
    }

    /** {@inheritDoc} */
    @Override public EnlistOperation operation() {
        return op;
    }

    /** {@inheritDoc} */
    public boolean hasNextX() {
        return it.hasNext();
    }

    /** {@inheritDoc} */
    public Object nextX() {
        return it.next();
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(GridDhtTxEnlistFuture.class, this);
    }

    /**  */
    public void sendTimestamp(long sendTimestamp) {
        this.sendTimestamp = sendTimestamp;
    }

    /**  */
    public void receiveTimestamp(long receiveTimestamp) {
        this.receiveTimestamp = receiveTimestamp;
    }

    /**
     * @return Request send timestamp.
     */
    public long sendTimestamp() {
        return sendTimestamp;
    }

    /**
     * @return Request receive timestamp.
     */
    public long receiveTimestamp() {
        return receiveTimestamp;
    }
}
