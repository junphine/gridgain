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

package org.apache.ignite.internal.processors.cache.persistence.tree;

import java.util.ArrayList;
import java.util.List;
import org.apache.ignite.internal.util.typedef.T2;

/**
 * This exception indicates that there's something wrong with B+Tree data integrity. Additional info about corrupted
 * pages is present in fields.
 */
public class BPlusTreeRuntimeException extends RuntimeException {
    /** */
    private static final long serialVersionUID = 0L;
    /** Group id common for all potentially corrupted pages. */
    private final int grpId;
    /** Ids of potentially corrupted pages. */
    private final long[] pageIds;

    /** */
    public BPlusTreeRuntimeException(Throwable cause, int grpId, long... pageIds) {
        super(cause);

        this.grpId = grpId;
        this.pageIds = pageIds;
    }

    /** Pairs of (groupId, pageId). */
    public List<T2<Integer, Long>> pages() {
        List<T2<Integer, Long>> res = new ArrayList<>(pageIds.length);

        for (long pageId : pageIds)
            res.add(new T2<>(grpId, pageId));

        return res;
    }
}
