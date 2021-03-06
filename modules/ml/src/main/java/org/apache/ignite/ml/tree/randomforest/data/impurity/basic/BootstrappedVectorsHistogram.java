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

package org.apache.ignite.ml.tree.randomforest.data.impurity.basic;

import java.util.Set;
import org.apache.ignite.ml.dataset.feature.BucketMeta;
import org.apache.ignite.ml.dataset.feature.ObjectHistogram;
import org.apache.ignite.ml.dataset.impl.bootstrapping.BootstrappedVector;

/**
 * Histogram for bootstrapped vectors with predefined bucket mapping logic for feature id == featureId.
 */
public abstract class BootstrappedVectorsHistogram extends ObjectHistogram<BootstrappedVector> {
    /** Serial version uid. */
    private static final long serialVersionUID = 6369546706769440897L;

    /** Bucket ids. */
    protected final Set<Integer> bucketIds;

    /** Bucket meta. */
    protected final BucketMeta bucketMeta;

    /** Feature id. */
    protected final int featureId;

    /**
     * Creates an instance of BootstrappedVectorsHistogram.
     *
     * @param bucketIds Bucket ids.
     * @param bucketMeta Bucket meta.
     * @param featureId Feature Id.
     */
    public BootstrappedVectorsHistogram(Set<Integer> bucketIds, BucketMeta bucketMeta, int featureId) {
        this.bucketIds = bucketIds;
        this.bucketMeta = bucketMeta;
        this.featureId = featureId;
    }

    /** {@inheritDoc} */
    @Override public Integer mapToBucket(BootstrappedVector vec) {
        int bucketId = bucketMeta.getBucketId(vec.features().get(featureId));
        this.bucketIds.add(bucketId);
        return bucketId;
    }
}
