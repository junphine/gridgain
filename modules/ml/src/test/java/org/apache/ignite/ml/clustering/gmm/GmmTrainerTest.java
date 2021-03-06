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

package org.apache.ignite.ml.clustering.gmm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.ignite.ml.common.TrainerTest;
import org.apache.ignite.ml.dataset.feature.extractor.Vectorizer;
import org.apache.ignite.ml.dataset.feature.extractor.impl.DoubleArrayVectorizer;
import org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder;
import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for GMM trainer.
 */
public class GmmTrainerTest extends TrainerTest {
    /** Data. */
    private static final Map<Integer, double[]> data = new HashMap<>();

    static {
        data.put(0, new double[] {1.0, 1.0, 1.0});
        data.put(1, new double[] {1.0, 2.0, 1.0});
        data.put(2, new double[] {2.0, 1.0, 1.0});
        data.put(3, new double[] {-1.0, -1.0, 2.0});
        data.put(4, new double[] {-1.0, -2.0, 2.0});
        data.put(5, new double[] {-2.0, -1.0, 2.0});
    }

    /** */
    @Test
    public void testFit() {
        GmmTrainer trainer = new GmmTrainer(2, 1)
            .withInitialMeans(Arrays.asList(
                VectorUtils.of(1.0, 2.0),
                VectorUtils.of(-1.0, -2.0)));
        GmmModel model = trainer.fit(
            new LocalDatasetBuilder<>(data, parts),
            new DoubleArrayVectorizer<Integer>().labeled(Vectorizer.LabelCoordinate.LAST)
        );

        Assert.assertEquals(2, model.countOfComponents());
        Assert.assertEquals(2, model.dimension());
        Assert.assertArrayEquals(new double[] {1.33, 1.33}, model.distributions().get(0).mean().asArray(), 1e-2);
        Assert.assertArrayEquals(new double[] {-1.33, -1.33}, model.distributions().get(1).mean().asArray(), 1e-2);
    }

    /** */
    @Test(expected = IllegalArgumentException.class)
    public void testOnEmptyPartition() throws Throwable {
        GmmTrainer trainer = new GmmTrainer(2, 1)
            .withInitialMeans(Arrays.asList(VectorUtils.of(1.0, 2.0), VectorUtils.of(-1.0, -2.0)));

        try {
            trainer.fit(
                new LocalDatasetBuilder<>(new HashMap<>(), parts),
                new DoubleArrayVectorizer<Integer>().labeled(Vectorizer.LabelCoordinate.LAST)
            );
        }
        catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    /** */
    @Test
    public void testUpdateOnEmptyDataset() {
        GmmTrainer trainer = new GmmTrainer(2, 1)
            .withInitialMeans(Arrays.asList(
                VectorUtils.of(1.0, 2.0),
                VectorUtils.of(-1.0, -2.0)));
        GmmModel model = trainer.fit(
            new LocalDatasetBuilder<>(data, parts),
            new DoubleArrayVectorizer<Integer>().labeled(Vectorizer.LabelCoordinate.LAST)
        );

        model = trainer.updateModel(model,
            new LocalDatasetBuilder<>(new HashMap<>(), parts),
            new DoubleArrayVectorizer<Integer>().labeled(Vectorizer.LabelCoordinate.LAST)
        );

        Assert.assertEquals(2, model.countOfComponents());
        Assert.assertEquals(2, model.dimension());
        Assert.assertArrayEquals(new double[] {1.33, 1.33}, model.distributions().get(0).mean().asArray(), 1e-2);
        Assert.assertArrayEquals(new double[] {-1.33, -1.33}, model.distributions().get(1).mean().asArray(), 1e-2);
    }
}
