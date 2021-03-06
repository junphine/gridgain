﻿/*
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

namespace Apache.Ignite.Core.Impl.Client.Cache.Query
{
    using Apache.Ignite.Core.Cache;
    using Apache.Ignite.Core.Impl.Binary.IO;
    using Apache.Ignite.Core.Impl.Cache;
    using Apache.Ignite.Core.Impl.Client;

    /// <summary>
    /// Client query cursor.
    /// </summary>
    internal sealed class ClientQueryCursor<TK, TV> : ClientQueryCursorBase<ICacheEntry<TK, TV>>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="ClientQueryCursor{TK, TV}" /> class.
        /// </summary>
        /// <param name="ignite">The ignite.</param>
        /// <param name="cursorId">The cursor identifier.</param>
        /// <param name="keepBinary">Keep binary flag.</param>
        /// <param name="initialBatchStream">Optional stream with initial batch.</param>
        /// <param name="getPageOp">The get page op.</param>
        public ClientQueryCursor(IgniteClient ignite, long cursorId, bool keepBinary,
            IBinaryStream initialBatchStream, ClientOp getPageOp)
            : base(ignite, cursorId, keepBinary, initialBatchStream, getPageOp,
                r => new CacheEntry<TK, TV>(r.ReadObject<TK>(), r.ReadObject<TV>()))
        {
            // No-op.
        }
    }
}
