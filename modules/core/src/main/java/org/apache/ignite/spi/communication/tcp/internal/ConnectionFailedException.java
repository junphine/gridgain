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

package org.apache.ignite.spi.communication.tcp.internal;

import java.util.UUID;
import org.apache.ignite.spi.IgniteSpiException;
import org.jetbrains.annotations.Nullable;

/** */
public class ConnectionFailedException extends IgniteSpiException {
    /** Serial version uid. */
    private static final long serialVersionUID = 0L;
    /** */
    public final UUID nodeId;
    /** */
    public final int connIdx;

    /** */
    public ConnectionFailedException(String msg, @Nullable Throwable cause, UUID nodeId, int connIdx) {
        super(msg, cause);
        this.nodeId = nodeId;
        this.connIdx = connIdx;
    }
}
