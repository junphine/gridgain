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

package org.apache.ignite.internal.processors.sql;

import org.apache.ignite.cache.CacheAtomicityMode;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.Test;

/** */
public class IgniteCacheReplicatedTransactionalSnapshotColumnConstraintTest
    extends IgniteCacheReplicatedAtomicColumnConstraintsTest {
    /** {@inheritDoc} */
    @NotNull @Override protected CacheAtomicityMode atomicityMode() {
        return CacheAtomicityMode.TRANSACTIONAL_SNAPSHOT;
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongStringValueFail() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongStringKeyFail() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongStringValueFieldFail() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongStringKeyFieldFail() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongStringKeyFail2() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongStringKeyFail3() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongDecimalValueFail() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongDecimalKeyFail() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongDecimalKeyFail2() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongDecimalValueFieldFail() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongDecimalValueFieldFail2() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongDecimalKeyFieldFail() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongDecimalValueScaleFail() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongDecimalKeyScaleFail() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongDecimalKeyScaleFail2() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongDecimalValueFieldScaleFail() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongDecimalValueFieldScaleFail2() {
        // No-op.
    }

    /** */
    @Ignore("https://issues.apache.org/jira/browse/IGNITE-10066")
    @Test
    @Override public void testPutTooLongDecimalKeyFieldScaleFail() {
        // No-op.
    }
}
